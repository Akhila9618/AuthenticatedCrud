import {
  createBrowserRouter,
  createRoutesFromElements,
  Navigate,
  Route,
} from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute";
import Dashboard from "./pages/Dashboard";
import Users from "./pages/Users";
import Login from "./components/login";
import NavbarPage from "./components/NavbarPage";
import EmployeeList from "./components/Employees/EmployeeList";
import AnualSalaryReports from "./components/Reports/AnualSalaryReports";
import AttendanceReports from "./components/Reports/AttendanceReports";
import NotFound from "./components/NotFound";

const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      <Route path="login" element={<Login />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<NavbarPage />}>
          <Route index element={<Navigate to="dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="employees">
            <Route index element={<EmployeeList />} />
          </Route>

          <Route path="reports">
            <Route index element={<Navigate to="salary" replace />} />
            <Route path="salary" element={<AnualSalaryReports />} />
            <Route path="attendance" element={<AttendanceReports />} />
          </Route>

          <Route path="*" element={<NotFound />} />
        </Route>
      </Route>
    </>,
  ),
);

export default router;
