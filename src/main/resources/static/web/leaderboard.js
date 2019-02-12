const main = new Vue({
    el: "#main",
    data: {
        gameData: [],
        games: [],
        firstPlayer: [],
        secondPlayer: [],
        boardData: [],
        loggedIn: false
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
            if (!main.loggedIn) {
                var username = document.getElementById("username");
                var password = document.getElementById("password");

                if (username.value !== "" && password.value !== "") {
                    fetch("/api/login", {
                            credentials: "include",
                            method: "POST",
                            headers: {
                                "Accept": "application/json",
                                "Content-Type": "application/x-www-form-urlencoded"
                            },
                            body: 'username=' + username.value + '&password=' + password.value
                        }).then(response => main.setLoggedIn(true))
                        .catch(error => console.error(error));
                } else {
                    console.log("Please fill out the forms.");
                }
            } else {
                console.log("You are already logged in.");
            }
        },
        logout() {
            if (main.loggedIn) {
                fetch("/api/logout", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                }).then(r => {
                    main.setLoggedIn(false);
                    main.clearFields();
                }).catch(function (error) {
                    console.log(error);
                })
            } else {
                console.log("You are not logged in.");
            }
        },
        signup() {
            var username = document.getElementById("username");
            var password = document.getElementById("password");

            fetch("/api/players", {
                credentials: 'include',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'username=' + username.value + '&password=' + password.value
            }).then(r => console.log(r)).catch(e => console.log(e))
        },
        setLoggedIn(value) {
            main.loggedIn = value;
        },
        clearFields() {
            document.getElementById("username").value = " ";
            document.getElementById("password").value = " ";
        }
    }
});
