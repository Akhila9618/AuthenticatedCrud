import React, { useEffect, useState } from "react";
import {
  deleteEmp,
  getAllEmployees,
  saveNewEmp,
  updateEmp,
} from "../../services/apiservice";

function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const formInitialState = {
    empName: "",
    empSalary: 1000,
    empAddress: "",
    password: "",
    empEmail: "",
    role: "",
    companyName: "psx",
  };
  const touchedInitialState = {
    empName: false,
    empEmail: false,
    empSalary: false,
    empAddress: false,
    password: false,
    role: false,
    companyName: false,
  };

  const [employee, setEmployee] = useState(formInitialState);
  const [touched, setTouched] = useState(touchedInitialState);
  const [tableHeaders, setTableHeadrs] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [saveType, setSaveType] = useState("add");
  const [roles, setRoles] = useState([]);
  const [openType, setOpenType] = useState("add");
  const handleChange = (e) => {
    setEmployee({
      ...employee,
      [e.target?.name]: e.target.value,
    });
  };
  const saveNewUser = async () => {
    try {
      console.log("emp", employee);
      let apiRes;
      if (saveType === "edit") {
        apiRes = await updateEmp(employee);
      } else {
        apiRes = await saveNewEmp(employee);
      }
      if (apiRes && apiRes.status === "success") {
        console.log(apiRes.message || "Employee saved successfully");
        setShowModal(false);
        setTouched(touchedInitialState);
        setEmployee(formInitialState);
        fetchEmp();
      } else {
        console.log(res.message || "An Error Occured");
      }
    } catch (error) {
      console.log(error.message || error.msg);
    }
  };

  const fetchEmp = async () => {
    try {
      const employeeRes = await getAllEmployees();
      setEmployees(employeeRes.data.empList);
      if (employeeRes.data && employeeRes.data?.empList.length > 0) {
        const headersList = [
          "Action",
          ...Object.keys(employeeRes.data.empList[0]),
        ];
        const filteredHeaders = headersList.filter(
          (header) => !["password", "emplId"].includes(header),
        );
        setTableHeadrs(filteredHeaders);
        setRoles(employeeRes?.data?.rolesList);
      }
    } catch (error) {
      console.log(error);
    }
  };
  const handleFormTouch = (e) => {
    setTouched((prev) => ({
      ...prev,
      [e.target?.name]: true,
    }));
  };
  const getValidationClass = (field) => {
    if (!touched[field]) return "";
    const value = employee?.[field].trim();

    return value ? "is-valid" : "is-invalid";
  };
  const openForm = (openType, data = {}) => {
    console.log(data);
    setOpenType(openType);
    setTouched(touchedInitialState);
    if (openType !== "edit") {
      setSaveType("add");
      setEmployee(formInitialState);
    } else {
      setSaveType("edit");
      setEmployee({
        emplId: data.emplId,
        empName: data.empName,
        empEmail: data.empEmail,
        empSalary: data.empSalary,
        empAddress: data.empAddress,
        role: data.role,
        companyName: data.companyName,
      });
    }
    setShowModal(true);
  };
  const deleteEmployee = async (empRow) => {
    try {
      const res = await deleteEmp(empRow.emplId);
      if (res) {
        if (res.status === "success") {
          fetchEmp();
        } else {
          console.log(res.message || "An Error Occured");
        }
      }
    } catch (error) {
      console.log(error.msg);
    }
  };

  useEffect(() => {
    fetchEmp();
  }, []);
  return (
    <>
      <div className="card shadow-sm border-0">
        <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">
          <h5 className="mb-0">
            <span className="material-icons align-middle me-2">groups</span>
            Employee Management
          </h5>

          <button className="btn btn-light" onClick={() => openForm("add")}>
            <span className="material-icons align-middle me-1">add</span>
            Add Employee
          </button>
        </div>
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-striped">
              <thead>
                <tr>
                  {tableHeaders.map((column) => (
                    <th key={column}>{column}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {employees.map((empRow, index) => (
                  <tr key={index}>
                    {tableHeaders.map((column) =>
                      column === "Action" &&
                      empRow.empName !== "defaultuser" ? (
                        <td key={column}>
                          <div>
                            <span
                              className="material-icons"
                              style={{ cursor: "pointer" }}
                              onClick={() => deleteEmployee(empRow)}
                            >
                              delete
                            </span>
                            <span
                              style={{ cursor: "pointer" }}
                              className="material-icons"
                              onClick={() => openForm("edit", empRow)}
                            >
                              edit
                            </span>
                          </div>
                        </td>
                      ) : (
                        <td key={column}>{empRow[column]}</td>
                      ),
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
      {showModal && (
        <div
          className="modal fade show"
          style={{ display: "block", backgroundColor: "rgba(0,0,0,0.5)" }}
        >
          <div className="modal-dialog">
            <div className="modal-content">
              <div
                className="modal-header bg-primary text-white"
                style={{ padding: "8px 16px !important" }}
              >
                <h5 className="modal-title">Add Employee</h5>
              </div>

              <div className="modal-body">
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label>Name</label>
                    <input
                      type="text"
                      className="form-control"
                      name="empName"
                      value={employee.empName}
                      onChange={handleChange}
                    />
                  </div>

                  <div className="col-md-6 mb-3">
                    <label>Email</label>
                    <input
                      type="email"
                      name="empEmail"
                      value={employee.empEmail}
                      onChange={handleChange}
                      className={`form-control ${getValidationClass("empEmail")}`}
                      // onBlur={handleFormTouch("empEmail")}
                      onBlur={(e) => handleFormTouch(e)}
                    />
                    <div className="invalid-feedback">Email is required.</div>
                  </div>

                  <div className="col-md-6 mb-3">
                    <label>Salary</label>
                    <input
                      type="number"
                      className="form-control"
                      name="empSalary"
                      value={employee.empSalary}
                      onChange={handleChange}
                    />
                  </div>

                  <div className="col-md-6 mb-3">
                    <label>Address</label>
                    <input
                      type="text"
                      className={`form-control ${getValidationClass("empAddress")}`}
                      name="empAddress"
                      value={employee.empAddress}
                      onChange={handleChange}
                      // onBlur={handleFormTouch("empAddress")}
                      onBlur={(e) => handleFormTouch(e)}
                    />
                    <div className="invalid-feedback">Address is required.</div>
                  </div>
                  {openType !== "edit" && (
                    <div className="col-md-6 mb-3">
                      <label>Password</label>

                      <span className="input-group">
                        <input
                          type={showPassword ? "text" : "password"}
                          className={`form-control ${getValidationClass("password")}`}
                          name="password"
                          value={employee.password}
                          onChange={handleChange}
                          onBlur={(e) => handleFormTouch(e)}
                        />

                        <span
                          className="input-group-text"
                          style={{ cursor: "pointer" }}
                          onMouseEnter={() => setShowPassword(true)}
                          onMouseLeave={() => setShowPassword(false)}
                        >
                          <span className="material-icons">
                            {showPassword ? "visibility_off" : "visibility"}
                          </span>
                        </span>
                        <div className="invalid-feedback">
                          Password is required.
                        </div>
                      </span>
                    </div>
                  )}

                  <div className="col-md-6 mb-3">
                    <label>Role</label>
                    <select
                      name="role"
                      className={`form-control ${getValidationClass("role")}`}
                      value={employee.role}
                      onChange={handleChange}
                    >
                      <option value="">Select Role</option>
                      {roles.map((role) => (
                        <option key={role} value={role.toLowerCase()}>
                          {role}
                        </option>
                      ))}
                    </select>
                    {/* <input
                      type="text"
                      className={`form-control ${getValidationClass("role")}`}
                      name="role"
                      required
                      value={employee.role}
                      onChange={handleChange}
                    /> */}
                  </div>

                  <div className="col-md-12 mb-3">
                    <label>Company Name</label>
                    <input
                      type="text"
                      className="form-control"
                      name="companyName"
                      value={employee.companyName}
                      onChange={handleChange}
                    />
                  </div>
                </div>
              </div>

              <div className="modal-footer">
                <button
                  className="btn btn-secondary"
                  onClick={() => {
                    setShowModal(false);
                  }}
                >
                  Cancel
                </button>

                <button
                  className="btn btn-primary"
                  onClick={() => saveNewUser()}
                >
                  Save Employee
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default EmployeeList;
