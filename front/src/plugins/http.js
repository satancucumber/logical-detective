import axios from "axios";

export default {
    install(Vue, options) {
        Vue.prototype.$http = axios.create({
            baseUrl: "http://host.docker.internal:8181",
            headers: options.headers || null
        })
    }
}