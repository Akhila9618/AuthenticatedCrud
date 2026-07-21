import { useEffect, useState } from "react";
import "./App.css";
import Login from "./components/login";
import { Outlet, Route, Routes } from "react-router-dom";

function App() {
  return (
    <>
      <Outlet />
    </>
  );
}

export default App;
