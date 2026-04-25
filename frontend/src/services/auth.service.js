import api from "./api";

const register = (data) => {
  return api.post("/auth/register", data);
};

const login = (data) => {
  return api.post("/auth/login", data).then((response) => {
    if (response.data.accessToken) {
      localStorage.setItem("user", JSON.stringify(response.data));
      localStorage.setItem("token", response.data.accessToken);
    }
    return response.data;
  });
};

const logout = () => {
  localStorage.removeItem("user");
  localStorage.removeItem("token");
};

const changePassword = (data) => {
  return api.post("/auth/change-password", data);
};

export default {
  register,
  login,
  logout,
  changePassword,
};
