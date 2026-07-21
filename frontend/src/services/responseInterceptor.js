import api from "./apiservice";

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response.status === 401) {
      localStorage.removeItem("loggedInUser");
      alert("Session Expired. Please login again.");
      window.location.href = "/auth/login";
    }
    return Promise.reject(error);
  },
);
