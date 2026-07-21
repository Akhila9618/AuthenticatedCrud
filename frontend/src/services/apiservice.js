import axios from "axios";
const base = "http://localhost:8080";
const api = axios.create({
  baseURL: base + "/employee/",
});

export  default api;
export const login = async (userCredentials) => {
  const res = await api.post("auth/login", userCredentials);
  return res.data;
};

export const getAllEmployees = async() => {
  const res =await api.get("employees/fetchAllEmployees");
  return res.data;
};
export const saveNewEmp = async(newUser) =>{
  const res = await api.post("employees/saveEmployee" , newUser);
  return res.data;
}
export const updateEmp = async(newUser) =>{
  const res = await api.put("employees/updateEmployee" , newUser);
  return res.data;
}

export const deleteEmp = async (id) =>{
  const res = await api.delete(`employees/deleteEmployee/${id}`);
  return res.data;
}

