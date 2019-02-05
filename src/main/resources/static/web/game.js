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
                for(let i = 0; i < this.gameData.games.gamePlayers.length; i++)
                    emails.push(this.gameData.games.gamePlayers[i].player.email);

                if(emails.length > 0)
                    this.currentUsers = emails[0] + " vs " + emails[1];

                document.getElementById("gameInfo").innerHTML = this.currentUsers;

            }).catch(function (error) {
                console.log(error);
            })
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
