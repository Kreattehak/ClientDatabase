(function () {

    var table = document.querySelector("#clientsTable"),
        ths = table.querySelectorAll("thead th"),
        trs = table.querySelectorAll("tbody tr");

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
                    if (!rd.test(a1)) return order === "asc" ? -1 : 1;
                    if (!rd.test(b1)) return order === "asc" ? 1 : -1;
                    if (a1 != b1) return order === "asc" ? a1 - b1 : b1 - a1;
                }
                else if (a1 != b1 && a1 > b1) return order === "asc" ? -1 : 1;
                else if (a1 != b1 && a1 < b1) return order === "asc" ? 1 : -1;
            }
            return order === "asc" ? a.length - b.length : b.length - a.length;
        });

        trsArr.forEach(function (tr) {
            df.appendChild(tr);
        });

        target.className = order;
        table.querySelector("tbody").appendChild(df);
    }

    for (var i = 0; i < ths.length; i++) {
        ths[i].onclick = sortBy;
    }

    Array.prototype.naturalSort = function () {
        var a, b, a1, b1, rx = /(\d+)|(\D+)/g, rd = /\d+/;
        return this.sort(function (as, bs) {
            a = String(as).toLowerCase().match(rx);
            b = String(bs).toLowerCase().match(rx);
            while (a.length && b.length) {
                a1 = a.shift();
                b1 = b.shift();
                if (rd.test(a1) || rd.test(b1)) {
                    if (!rd.test(a1)) return 1;
                    if (!rd.test(b1)) return -1;
                    if (a1 != b1) return a1 - b1;
                }
                else if (a1 != b1) return a1 > b1 ? 1 : -1;
            }
            return a.length - b.length;
        });
    }
})();