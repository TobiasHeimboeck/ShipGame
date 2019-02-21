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
        ship: []
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
                        document.getElementById("P" + currentLoc).style.backgroundColor = "cyan";
                    }
                }

                for (let c = 0; c < this.gameData.salvoes.length; c++) {
                    for (let d = 0; d < this.gameData.salvoes[c].locations.length; d++) {
                        let currentSalvo = this.gameData.salvoes[c];
                        document.getElementById("E" + currentSalvo.locations[d]).style.backgroundColor = "orange";
                        document.getElementById("E" + currentSalvo.locations[d]).innerHTML = currentSalvo.turn;
                        document.getElementById("E" + currentSalvo.locations[d]).style.textAlign = "center";
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
        getShipLocations() {
            for (let a = 0; a < this.gameData.ships.length; a++) {
                for (let b = 0; b < this.gameData.ships[a].locations.length; b++) {
                    let currentLoc = this.gameData.ships[a].locations[b];
                    document.getElementById("P" + currentLoc).style.backgroundColor = "cyan";
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
            return decodeURIComponent(results[2].replace(/\+/g, ' '));
        },
        loadPage(search) {
            var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

            search.replace(reg, function (match, param, val) {
                this.pageNumber[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
            });

            return this.pageNumber;
        },
        placeShips() {
            fetch("/api/games/players/" + main.getParameterByName("gp") + "/ships", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify([
                        {
                            type: "destroyer",
                            locations: ["A5", "B1", "C1"]
                    }
                ])
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
        goBack() {
            location.href = "games.html";
        }
    }
});

document.getElementById("player").addEventListener("click", function () {
    if (main.placing) {
        var id = event.target.id;
        var regex = /\d+/;

        if (myonoffswitch.checked) {

            document.getElementById(id).style.backgroundColor = "cyan";

            for (var i = id.match(regex); i < parseInt(id.match(regex)) + parseInt(main.lengthToPlace); i++) {
                var replaced = id.replace(/[0-9]/g, '');
                document.getElementById(replaced + i).style.backgroundColor = "cyan";
            }

            main.lengthToPlace = 0;
            main.placing = false;

        } else {

            var cell = main.columns[parseInt(id.match(regex)) - 1];

            // console.log(id.replace(/[0-9]/g, ''));

            var index = 0;
            var response = [];
            
            do {
                if(response.length === 0)
                    response.push(id.replace(/[0-9]/g, ''));
                else
                    response.push(main.nextLetter(response[response.length -1]));
                index++;
            } while(index < main.lengthToPlace); 
            
            for(var i = 0; i < response.length; i++) {
                var realID = response[i] + cell;
                console.log(realID);
                document.getElementById(realID).style.backgroundColor = "cyan";
            }
            
            response = [];
            console.log(response);

            main.lengthToPlace = 0;
            main.placing = false;
        }
    }
})
