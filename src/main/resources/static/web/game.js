const main = new Vue({
    el: "#main",
    data: {
        pageNumber: {},
        gameData: {},
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        columns: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
        currentUsers: undefined
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

                var emails = [];
                for (let i = 0; i < this.gameData.games.gamePlayers.length; i++) {
                    let current = this.gameData.games.gamePlayers[i].player;
                    if (main.getParameterByName("gp").toString() === current.id.toString()) {
                        emails.push(current.email + " (you) ");
                    } else {
                        emails.push(current.email);
                    }
                }

                if (emails.length > 0)
                    this.currentUsers = emails[0] + " vs " + emails[1];

                document.getElementById("gameInfo").innerHTML = this.currentUsers;
                
                for(let a = 0; a < this.gameData.ships.length; a++) {
                    for(let b = 0; b < this.gameData.ships[a].locations.length; b++) {
                        let currentLoc = this.gameData.ships[a].locations[b];
                        document.getElementById(currentLoc).style.backgroundColor = "cyan";
                    }
                }

            }).catch(function (error) {
                console.log(error);
            })
        },
        getShips() {
            console.log(this.gameData.ships);
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
