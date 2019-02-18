const main = new Vue({
    el: "#main",
    data: {
        gameData: [],
        games: [],
        players: [],
        boardData: [],
        loggedIn: false,
        username: undefined,
        usernames: [],
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

                for (let i = 0; i < main.games.games.length; i++) {
                    main.players = main.games.games[i].gamePlayers;

                    for (let a = 0; a < main.games.games[i].gamePlayers.length; a++) {
                        main.usernames.push(main.games.games[i].gamePlayers[a].name);
                    }
                }

            }).catch(function (error) {
                console.log(error);
            })
        },
        login() {
            if (!main.loggedIn) {
                var username = document.getElementById("username");
                var password = document.getElementById("password");

                if (main.usernames.indexOf(username.value) > -1) {

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

                        main.username = username.value;

                    } else {
                        alert("Please fill out the forms.");
                    }

                } else {
                    alert("This username doesn't exist.");
                }

            } else {
                alert("You are already logged in.");
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
                }).catch(function (error) {
                    console.log(error);
                })
            } else {
                alert("You are not logged in.");
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
            }).then(r => {
                main.setLoggedIn(true);
            }).catch(function (error) {
                console.log(error);
            })
        },
        createGame() {
            var username = document.getElementById("username");
            var password = document.getElementById("password");

            fetch("/api/games", {
                credentials: 'include',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'username=' + main.username
            }).then(response => {
                if(response.status === 401) {
                    alert("Please log in!");
                } else if (response.status === 201) {
                    return response.json();
                }
            })
            .then(json => location.href = "game.html?gp=" + json.gpid)
            .catch(e => console.log(e));
        },
        setLoggedIn(value) {
            main.loggedIn = value;
        }
    }
});