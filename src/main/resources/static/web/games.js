const main = new Vue({
    el: "#main",
    data: {
        jsonData: [],
    },
    created() {
        this.jsonData = this.getData();
    },
    methods: {
        getData() {
            fetch("/api/games", {
                method: "GET",
                credentials: "include",
            }).then(r => r.json()).then(json => {
                main.jsonData = json;
            }).catch(e => console.log(e));
        }
    }
});