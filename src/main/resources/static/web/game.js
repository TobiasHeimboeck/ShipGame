const main = new Vue({
    el: "#main",
    data: {
        pageNumber: {},
        gameData: {},
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        columns: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        player: {},
        enemy: {}
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
                this.gameData = data;

                for (let x = 0; x < this.gameData.games.gamePlayers.length; x++) {
                    let currentPlayer = this.gameData.games.gamePlayers[x].player;
                    if (main.getParameterByName("gp").toString() === currentPlayer.id.toString()) {
                        this.player = currentPlayer;
                    } else {
                        this.enemy = currentPlayer;
                    }
                }

                document.getElementById("gameInfo").innerHTML = this.player.email + "(you) vs " + this.enemy.email;

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
                    }
                }

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
    }
});
