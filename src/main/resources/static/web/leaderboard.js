const main = new Vue({
    el: "#main",
    data: {
        gameData: {}
    },
    created() {
       this.startFetchingAsync("/api/scoreboard");
    },
    methods: {
        startFetchingAsync(url) {
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
    }
});
