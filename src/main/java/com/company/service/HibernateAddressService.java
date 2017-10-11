package com.company.service;

import com.company.dao.AddressDao;
import com.company.model.Address;
import com.company.model.Client;
import com.company.util.InjectLogger;
import com.company.util.ProcessUserRequestException;
import com.company.util.WebDataResolverAndCreator;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.company.util.Mappings.ADDRESS_SERVICE_LOGGER_NAME;
import static com.company.util.Mappings.ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class HibernateAddressService implements AddressService {

    @Value("${exception.findAddress}")
    private String findAddressExceptionMessage;
    @Value("${exception.saveAddress}")
    private String saveAddressExceptionMessage;
    @Value("${exception.deleteAddress}")
    private String deleteAddressExceptionMessage;
    @Value("${exception.updateAddress}")
    private String updateAddressExceptionMessage;
    @Value("${exception.updateMainAddress}")
    private String updateMainAddressExceptionMessage;
    @Value("${exception.deleteAddressNoRefererHeader}")
    private String deleteAddressNoRefererExceptionMessage;

    @InjectLogger(ADDRESS_SERVICE_LOGGER_NAME)
    private static Logger logger;

    private final AddressDao addressDao;
    private final ClientService clientService;
    private final WebDataResolverAndCreator webDataResolverAndCreator;

    private final Collector<Address, ?, Map<Long, String>> collectAddressesToMapWithLocation =
            Collectors.toMap(Address::getId, (address) -> address.getCityName() + ", " + address.getStreetName());

    @Autowired
    public HibernateAddressService(AddressDao addressDao, ClientService clientService,
                                   WebDataResolverAndCreator webDataResolverAndCreator) {
        this.addressDao = addressDao;
        this.clientService = clientService;
        this.webDataResolverAndCreator = webDataResolverAndCreator;
    }

    @Override
    public Address findAddressById(Long addressId, HttpServletRequest request) {
        Address addressFromDatabase = addressDao.findById(addressId);
        boolean isRequestProper = (addressFromDatabase != null);
        if (!isRequestProper) {
            logger.warn("{} tried to get address with id {}, but that address doesn't exist. "
                            + "This request was handmade.",
                    webDataResolverAndCreator.getUserData(request), addressId);
            throw new ProcessUserRequestException(findAddressExceptionMessage);
        }

        return addressFromDatabase;
    }

    @Override
    public Set<Address> findAllAddresses() {
        return new HashSet<>(addressDao.findAll());
    }

    @Override
    public Set<Address> getAllClientAddresses(Client client) {
        return new HashSet<>(client.getAddress());
    }

    @Override
    public Address[] getAllClientAddressesAsArray(Long clientId, HttpServletRequest request) {
        Client client = clientService.findClientById(clientId, request);
        Address[] addresses = getAllClientAddresses(client).toArray(new Address[0]);
        Arrays.sort(addresses, Comparator.comparing(Address::getId));

        return addresses;
    }

    @Override
    public Map<Long, String> getAllClientAddressesAsMap(Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = clientService.findClientById(clientId, request);

        return clientFromDatabase.getAddress()
                .stream()
                .collect(collectAddressesToMapWithLocation);
    }

    @Override
    public Map<Long, String> getAllClientAddressesWithoutMainAddressAsMap(Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = clientService.findClientById(clientId, request);
        Long mainAddressId = clientFromDatabase.getMainAddress().getId();

        return clientFromDatabase.getAddress()
                .stream()
                .filter(address -> !Objects.equals(address.getId(), mainAddressId))
                .collect(collectAddressesToMapWithLocation);
    }

    @Override
    @Transactional(readOnly = false)
    public Address saveAddress(Address newAddress, Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = clientService.findClientById(clientId, request);
        Long addresses = checkIfAddressAlreadyExsists(clientFromDatabase, newAddress);
        boolean isRequestProper = (addresses <= 0);
        if (!isRequestProper) {
            logger.warn("{} tried to add address for client with id {}. " +
                            "This request was handmade, with data: clientId= {}!",
                    webDataResolverAndCreator.getUserData(request), clientId, clientId);
            throw new ProcessUserRequestException(saveAddressExceptionMessage);
        }

        Address addressStoredInDatabase = addressDao.save(newAddress);
        clientFromDatabase.addAddress(newAddress);
        newAddress.setClient(clientFromDatabase);

        logger.info("New address with id {} was added for client with id {}",
                addressStoredInDatabase.getId(), clientId);
        logger.trace("New {} was added for Client {} {} with id {}",
                addressStoredInDatabase, clientFromDatabase.getFirstName(),
                clientFromDatabase.getLastName(), clientFromDatabase.getId());

        return addressStoredInDatabase;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAddress(Long addressId, HttpServletRequest request) {
        Long clientId = webDataResolverAndCreator.fetchClientIdFromRequest(request);
        if (clientId.equals(ID_NOT_FOUND)) {
            logger.warn(webDataResolverAndCreator.getUserData(request)
                    + " tried to remove address, but request doesn't have a referer header!");
            throw new ProcessUserRequestException(deleteAddressNoRefererExceptionMessage);
        }
        Client clientFromDatabase = clientService.findClientById(clientId, request);
        boolean isRequestProper = !Objects.equals(clientFromDatabase.getMainAddress().getId(), addressId);
        if (!isRequestProper) {
            logger.warn("{} tried to delete address for client with id {}. "
                            + "This request was handmade, with data: addressId= {}, clientId= {}!",
                    webDataResolverAndCreator.getUserData(request), clientId, addressId, clientId);
            throw new ProcessUserRequestException(deleteAddressExceptionMessage);
        }
        Address addressToBeRemoved = findAddressById(addressId, request);

        clientFromDatabase.removeAddress(addressToBeRemoved);
        addressToBeRemoved.setClient(null);
        addressDao.delete(addressToBeRemoved);

        logger.info("Address with id " + addressId + " was deleted from client with id " + clientId);
        logger.trace(addressToBeRemoved + " was deleted from " + "Client "
                + clientFromDatabase.getFirstName() + " " + clientFromDatabase.getLastName()
                + " with id " + clientFromDatabase.getId());
    }

    //TODO: CHECK IF ADDRESS ALREADY EXISTS
    @Override
    @Transactional(readOnly = false)
    public Address updateAddress(Address editedAddress, HttpServletRequest request) {
        Long addressId = editedAddress.getId();
        boolean isRequestProper = (addressId != null);
        if (!isRequestProper) {
            logger.warn("{} tried to address. This request was handmade,with data: {}",
                    webDataResolverAndCreator.getUserData(request), editedAddress);
            throw new ProcessUserRequestException(updateAddressExceptionMessage);
        }
        Address addressFromDatabase = findAddressById(addressId, request);

        String addressData = addressFromDatabase.toString();
        addressFromDatabase.setCityName(editedAddress.getCityName());
        addressFromDatabase.setStreetName(editedAddress.getStreetName());
        addressFromDatabase.setZipCode(editedAddress.getZipCode());

        logger.info("Address with id {} was edited with data streetName={}, cityName={}, zipCode={}",
                editedAddress.getId(), addressFromDatabase.getStreetName(), addressFromDatabase.getCityName(),
                addressFromDatabase.getZipCode());
        logger.trace("{} was edited with data {}", addressData, addressFromDatabase);

        return addressFromDatabase;
    }


    @Override
    @Transactional(readOnly = false)
    public void updateMainAddress(Long addressId, Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = clientService.findClientById(clientId, request);
        boolean isRequestProper = !Objects.equals(clientFromDatabase.getMainAddress().getId(), addressId);
        if (!isRequestProper) {
            logger.warn("{} tried to edit main address for client with id {}. "
                            + "This request was handmade, with data: addressId= {}, clientId= {}!",
                    webDataResolverAndCreator.getUserData(request), clientId, addressId, clientId);
            throw new ProcessUserRequestException(updateMainAddressExceptionMessage);
        }
        Address addressFromDatabase = findAddressById(addressId, request);

        String clientData = "Client " + clientFromDatabase.getFirstName() + " "
                + clientFromDatabase.getLastName() + " with id " + clientFromDatabase.getId();
        clientFromDatabase.setMainAddress(addressFromDatabase);

        logger.info("Address with id {} was set as main address for client with id {}",
                addressId, clientId);
        logger.trace("{} was set as main address for {}", addressFromDatabase, clientData);
    }

    private Long checkIfAddressAlreadyExsists(Client client, Address newAddress) {
        return client.getAddress()
                .stream()
                .filter(address -> Objects.equals(address.getStreetName(), newAddress.getStreetName()) &&
                        Objects.equals(address.getZipCode(), newAddress.getZipCode()) &&
                        Objects.equals(address.getCityName(), newAddress.getCityName()))
                .count();
    }
}