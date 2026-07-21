import api from "./apiservice";

api.interceptors.request.use(
    config => {
        const token =JSON.parse(localStorage.getItem("loggedInUser") || "{}")?.token;
        if(token){
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config
    },
    error =>  Promise.reject(error)
);