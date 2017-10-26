(function () {

    var table = document.querySelector("#clientsTable"),
        ths = table.querySelectorAll("thead th"),
        trs = table.querySelectorAll("tbody tr"),
        bAddAddress = document.querySelector("#bAddAddress"),
        bEditClient = document.querySelector("#bEditClient"),
        bEditAddresses = document.querySelector("#bEditAddresses"),
        bEditMainAddress = document.querySelector("#bEditMainAddress"),
        bRemoveClient = document.querySelector("#bRemoveClient"),
        bRemoveAddress = document.querySelector("#bRemoveAddress"),
        activeRow;

    function makeArray(nodeList) {
        var arr = [];
        for (var i = 0; i < nodeList.length; i++) {
            arr.push(nodeList[i]);
        }
        return arr;
    }

    function clearClassName(nodeList) {
        for (var i = 0; i < nodeList.length; i++) {
            nodeList[i].className = "";
        }
    }

    function sortBy(e) {
        var target = e.target,
            thsArr = makeArray(ths),
            trsArr = makeArray(trs),
            index = thsArr.indexOf(target),
            df = document.createDocumentFragment(),
            order = (target.className === "" || target.className === "desc") ? "asc" : "desc";

        clearClassName(ths);

        var a, b, a1, b1, rx = /(\d+)|(\D+)/g, rd = /\d+/;

        trsArr.sort(function (as, bs) {
            a = String(as.children[index].textContent).toLowerCase().match(rx);
            b = String(bs.children[index].textContent).toLowerCase().match(rx);
            while (a.length && b.length) {
                a1 = a.shift();
                b1 = b.shift();
                if (rd.test(a1) || rd.test(b1)) {
                    if (!rd.test(a1)) return order === "asc" ? 1 : -1;
                    if (!rd.test(b1)) return order === "asc" ? -1 : 1;
                    if (a1 != b1) return order === "asc" ? a1 - b1 : b1 - a1;
                }
                else if (a1 != b1 && a1 > b1) return order === "asc" ? 1 : -1;
                else if (a1 != b1 && a1 < b1) return order === "asc" ? -1 : 1;
            }
            return 0;
        });

        trsArr.forEach(function (tr) {
            df.appendChild(tr);
        });

        target.className = order;
        table.querySelector("tbody").appendChild(df);
    }

    function markAsActive(e) {
        var classList = e.target.parentNode.classList;
        if (!classList.contains("success")) {
            clearClassName(trs);
            e.target.parentNode.classList.add("success");
            activeRow = e.target.parentNode;
        } else {
            classList.remove("success")
            activeRow = undefined;
        }

        if (activeRow && !activeRow.children[4].innerText) {
            disableAddressButtons();
        } else if(!activeRow || activeRow.children[4].innerText){
            enableAddressButtons();
        }
    }

    function disableAddressButtons() {
        bRemoveAddress.disabled = true;
        bEditMainAddress.disabled = true;
    }

    function enableAddressButtons() {
        bRemoveAddress.disabled = false;
        bEditMainAddress.disabled = false;
    }

    function showErrors(button) {
        var ul = document.querySelector("ul.errors");

        if (!ul) {
            ul = document.createElement("ul");
            ul.classList.add("errors");
            ul.classList.add("center-block");
        }

        ul.innerHTML = "";

        var li = document.createElement("li");

        li.textContent = "Please select a row";

        ul.appendChild(li);

        table.parentNode.insertBefore(ul, table);
    }

    for (var i = 0; i < ths.length; i++) {
        ths[i].onclick = sortBy;
    }

    for (var i = 0; i < trs.length; i++) {
        trs[i].onclick = markAsActive;
    }

    function addFunctionalityToButton(button, address) {
        button.addEventListener("click", function () {
            if (activeRow !== undefined) {
                window.location = "/" + address + "?clientId=" + activeRow.children[0].textContent;
            } else {
                showErrors(button);
            }
        }, false);
    }

    addFunctionalityToButton(bAddAddress, "admin/addAddress");
    addFunctionalityToButton(bEditClient, "admin/editClient");
    addFunctionalityToButton(bEditAddresses, "admin/editAddresses");
    addFunctionalityToButton(bEditMainAddress, "admin/editMainAddress");
    addFunctionalityToButton(bRemoveClient, "admin/removeClient");
    addFunctionalityToButton(bRemoveAddress, "admin/removeAddressFromClient");

})();