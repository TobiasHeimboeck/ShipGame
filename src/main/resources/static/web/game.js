var main = new Vue({
    el: "#main",
    data: {
        pageNumber: {},
        gameData: {},
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        columns: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        player: {},
        enemy: {},
        placing: false,
        lengthToPlace: 0,
        ship: [],
        ships: [],
        currentType: "",
        salvoLocations: [],
        shots: 5
    },
    created() {
        this.loadPage("gp");
        this.startFetchingAsync("/api/game_view/" + this.getParameterByName("gp"));
    },
    methods: {
        startFetchingAsync(url) {
            fetch(url, {
                method: "GET",
            }).then(function (response) {

                return response.json();

            }).then(function (json) {

                data = json;
                this.gameData = data.gameview;

                for (let x = 0; x < this.gameData.games.gamePlayers.length; x++) {
                    let currentPlayer = this.gameData.games.gamePlayers[x];
                    if (main.getParameterByName("gp").toString() === currentPlayer.id.toString()) {
                        main.player = currentPlayer;
                    } else {
                        main.enemy = currentPlayer;
                    }
                }

                if (main.enemy.player === undefined)
                    document.getElementById("gameInfo").innerHTML = main.player.player.email + "(you) Waiting...";
                else
                    document.getElementById("gameInfo").innerHTML = main.player.player.email + "(you) " + main.enemy.player.email;

                for (let a = 0; a < this.gameData.ships.length; a++) {
                    for (let b = 0; b < this.gameData.ships[a].locations.length; b++) {
                        let currentLoc = this.gameData.ships[a].locations[b];
                        document.getElementById(currentLoc).style.backgroundColor = "cyan";
                        document.getElementById(currentLoc).setAttribute("hasShip", true);
                    }
                }

                for (let c = 0; c < this.gameData.salvoes.length; c++) {
                    for (let d = 0; d < this.gameData.salvoes[c].locations.length; d++) {
                        let currentSalvo = this.gameData.salvoes[c];

                        for (var e = 0; e < this.gameData.infos.player_hitted_ships.length; e++) {

                            for (var f = 0; f < currentSalvo.locations.length; f++) {

                                if (this.gameData.infos.player_hitted_ships[e] === currentSalvo.locations[f]) {

                                    document.getElementById(currentSalvo.locations[f]).style.backgroundColor = "yellow";
                                    document.getElementById(currentSalvo.locations[f]).setAttribute("cell-hitted", true);
                                    document.getElementById(currentSalvo.locations[f]).innerHTML = currentSalvo.turn;
                                    document.getElementById(currentSalvo.locations[f]).style.textAlign = "center";

                                } else {

                                    document.getElementById(currentSalvo.locations[d]).style.backgroundColor = "orange";
                                    document.getElementById(currentSalvo.locations[d]).setAttribute("cell-hitted", true);
                                    document.getElementById(currentSalvo.locations[d]).innerHTML = currentSalvo.turn;
                                    document.getElementById(currentSalvo.locations[d]).style.textAlign = "center";

                                }
                            }
                        }
                    }
                }

                if (this.gameData.enemy_salvoes !== undefined) {
                    for (let e = 0; e < this.gameData.enemy_salvoes.length; e++) {
                        for (let f = 0; f < this.gameData.enemy_salvoes[e].locations.length; f++) {

                            let currentEnemySalvo = this.gameData.enemy_salvoes[e];

                            for (let a = 0; a < this.gameData.ships.length; a++) {
                                for (let b = 0; b < this.gameData.ships[a].locations.length; b++) {
                                    let currentLoc = this.gameData.ships[a].locations[b];

                                    if (currentEnemySalvo.locations[f] == currentLoc) {
                                        document.getElementById("P" + currentEnemySalvo.locations[f]).style.backgroundColor = "red";
                                        document.getElementById("P" + currentEnemySalvo.locations[f]).innerHTML = currentEnemySalvo.turn;
                                    }
                                }
                            }
                        }
                    }
                }

            }).catch(function (error) {
                console.log(error);
            })
        },
        addIdsToTable() {
            for (var i = 0; i < document.getElementsByTagName("tr").length; i++) {
                var curr = document.getElementsByTagName("tr")[i];
                if (curr.hasAttribute("rowable")) {
                    curr.setAttribute("data-id", i);
                    curr.setAttribute("data-letter", this.rows[i - 1]);
                }
            }
        },
        getShipLocations() {
            for (let a = 0; a < this.gameData.ships.length; a++) {
                for (let b = 0; b < this.gameData.ships[a].locations.length; b++) {
                    document.getElementById("P" + this.gameData.ships[a].locations[b]).style.backgroundColor = "cyan";
                }
            }
        },
        getParameterByName(name, url) {
            if (!url) url = window.location.href;
            name = name.replace(/[\[\]]/g, '\\$&');
            var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
                results = regex.exec(url);
            if (!results) return null;
            if (!results[2]) return '';
            this.addIdsToTable();
            return decodeURIComponent(results[2].replace(/\+/g, ' '));
        },
        loadPage(search) {
            var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

            search.replace(reg, function (match, param, val) {
                this.pageNumber[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
            });

            return this.pageNumber;
        },
        placeShips(ships) {
            fetch("/api/games/players/" + main.getParameterByName("gp") + "/ships", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(ships)
                }).then(response => {
                    if (response.status === 201) {
                        location.reload();
                    }
                })
                .catch(e => console.log(e));
        },
        placeSalvos(salvos) {
            fetch("/api/games/players/" + main.getParameterByName("gp") + "/salvos", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(salvos)
                }).then(response => {
                    if (response.status === 201) {
                        location.reload();
                    }
                })
                .catch(e => console.log(e));
        },
        getValue(current, id) {
            if (document.getElementById(id).innerHTML === "1") {
                if (!main.placing) {
                    main.placing = true;
                    main.lengthToPlace = current.getAttribute("data-length");
                    document.getElementById(id).innerHTML = "0";
                    main.currentType = current.getAttribute("data-type");
                }
            }
        },
        nextLetter(s) {
            return s.replace(/([a-iA-I])[^a-iA-I]*$/, function (a) {
                var c = a.charCodeAt(0);
                switch (c) {
                    case 90:
                        return 'A';
                    case 122:
                        return 'a';
                    default:
                        return String.fromCharCode(++c);
                }
            });
        },
        done() {
            main.placeShips(main.ships);
        },
        doneSalvos() {
            main.placeSalvos(main.salvoLocations);
        },
        goBack() {
            location.href = "games.html";
        }
    }
});

document.getElementById("enemy").addEventListener("click", function () {

    var id = event.target.id;

    if (document.getElementById(id) !== null) {
        if (main.shots > 0) {
            if (!document.getElementById(id).hasAttribute("cell-hitted")) {
                main.shots--;
                document.getElementById(id).style.backgroundColor = "orange";
                document.getElementById(id).setAttribute("cell-hitted", true);
                document.getElementById(id).style.textAlign = "center";
                document.getElementById(id).innerHTML = "?";
                main.salvoLocations.push(id);
            } else {
                document.getElementById(id).style.backgroundColor = "red";
                setTimeout(function () {
                    document.getElementById(id).style.backgroundColor = "orange";
                }, 100);
            }
        } else {
            if (!document.getElementById(id).hasAttribute("cell-hitted")) {
                document.getElementById(id).style.backgroundColor = "red";
                setTimeout(function () {
                    document.getElementById(id).style.backgroundColor = "white";
                }, 100);
            } else {
                document.getElementById(id).style.backgroundColor = "red";
                setTimeout(function () {
                    document.getElementById(id).style.backgroundColor = "orange";
                }, 100);
            }
        }
    }
})

document.getElementById("player").addEventListener("click", function () {
    if (main.placing) {
        var id = event.target.id;
        var regex = /\d+/;

        if (!document.getElementById(id).hasAttribute("hasShip")) {

            if (myonoffswitch.checked) {

                var cell = main.columns[parseInt(id.match(regex)) - 1];

                if (11 - cell < main.lengthToPlace) {
                    console.log("Ship is to long!");
                } else {

                    var cellsToPlace = [];

                    for (var i = id.match(regex); i < parseInt(id.match(regex)) + parseInt(main.lengthToPlace); i++) {
                        var replaced = id.replace(/[0-9]/g, '');
                        if (!document.getElementById(replaced + i).hasAttribute("hasShip")) {
                            cellsToPlace.push(replaced + i);
                        } else {
                            cellsToPlace = [];
                        }
                    }

                    if (cellsToPlace.length > 0) {

                        document.getElementById(id).style.backgroundColor = "cyan";

                        for (var cell1 = 0; cell1 < cellsToPlace.length; cell1++) {
                            document.getElementById(cellsToPlace[cell1]).style.backgroundColor = "cyan";
                            document.getElementById(cellsToPlace[cell1]).setAttribute("hasShip", true);
                        }

                        main.ships.push({
                            type: main.currentType,
                            locations: cellsToPlace
                        });

                        response = [];
                        main.currentType = "";

                        main.lengthToPlace = 0;
                        main.placing = false;
                    } else {
                        console.log("No cells to place. There is already a ship placed.");
                    }
                }

            } else {

                var cell = main.columns[parseInt(id.match(regex)) - 1];
                var index = 0;
                var response = [];

                var rowID = 0;

                for (var r = 0; r < document.getElementsByTagName("tr").length; r++) {
                    var curr = document.getElementsByTagName("tr")[r];
                    if (curr.hasAttribute("rowable")) {
                        if ("P" + curr.getAttribute("data-letter") === id.replace(/[0-9]/g, '')) {
                            rowID = curr.getAttribute("data-id");
                        }
                    }
                }

                if (11 - rowID < main.lengthToPlace) {
                    console.log("Ship is to long!");
                } else {

                    do {
                        if (response.length === 0) {
                            response.push(id.replace(/[0-9]/g, ''));
                        } else {
                            response.push(main.nextLetter(response[response.length - 1]));
                        }
                        index++;
                    } while (index < main.lengthToPlace);

                    var cellsToPlace = []

                    for (var i = 0; i < response.length; i++) {
                        var realID = response[i] + cell;
                        if (!document.getElementById(realID).hasAttribute("hasShip")) {
                            cellsToPlace.push(realID);
                        } else {
                            cellsToPlace = [];
                        }
                    }

                    if (cellsToPlace.length > 0) {

                        for (var cell1 = 0; cell1 < cellsToPlace.length; cell1++) {
                            document.getElementById(cellsToPlace[cell1]).style.backgroundColor = "cyan";
                            document.getElementById(cellsToPlace[cell1]).setAttribute("hasShip", true);
                        }

                        main.ships.push({
                            type: main.currentType,
                            locations: cellsToPlace
                        });

                        response = [];
                        main.currentType = "";

                        main.lengthToPlace = 0;
                        main.placing = false;
                    } else {
                        console.log("No cells to place. There is already a ship placed.");
                    }
                }
            }
        } else {
            document.getElementById(id).style.backgroundColor = "red";
            setTimeout(function () {
                document.getElementById(id).style.backgroundColor = "cyan";
            }, 100);
        }
    }
})
