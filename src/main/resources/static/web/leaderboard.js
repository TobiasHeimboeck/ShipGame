const main = new Vue({
    el: "#main",
    data: {
        gameData: [],
        games: [],
        firstPlayer: [],
        secondPlayer: []
    },
    created() {
        this.fetchScores("/api/scoreboard");
        this.fetchGames("/api/games");
    },
    methods: {
        fetchScores(url) {
            fetch(url, {
                method: "GET",
            }).then(function (response) {

                return response.json();

            }).then(function (json) {

                data = json;
                main.gameData = data;

            }).catch(function (error) {
                console.log(error);
            })
        },
        fetchGames(url) {
            fetch(url, {
                method: "GET",
            }).then(function (response) {

                return response.json();

            }).then(function (json) {

                data = json;
                main.games = data;

                for (let i = 0; i < main.games.length; i++) {
                    main.firstPlayer = main.games[i].gamePlayers[0];
                    main.secondPlayer = main.games[i].gamePlayers[1];
                }

            }).catch(function (error) {
                console.log(error);
            })
        },
        login() {
            fetch("/api/login", {
                credentials: 'include',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'username=tobiasheimboeck@outlook.com&password=gregbsej'
            }).then(response => {
                if (response.status == 200) {
                    console.log(response)
                }
            }).catch(error => {
                console.log(error);
            })
        }
    }
});
