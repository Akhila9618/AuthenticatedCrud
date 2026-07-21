import React, { useEffect, useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../services/AuthContext";

function NavbarPage() {
  const navigate = useNavigate();
  const { user, menus, logout } = useAuth();
  const [selectedMenu, setSelectedMenu] = useState({});
  const [selectedSubMenu, setSelectedSubMenu] = useState({});
  const handleLogOut = () => {
    logout();
    navigate("/login");
  };
  const handleSubMenuSelectionOnMenunChange = (menu) => {
    setSelectedMenu(menu);
    setSelectedSubMenu(menu.subPages[0]);
    navigate(menu.subPages[0].routingUrl);
  };

  useEffect(() => {
    if (user?.menusData?.length) {
      const firstMenu = user.menusData[0];
      setSelectedMenu(firstMenu);
      if (firstMenu.subPages?.length) {
        const firstSubMenu = firstMenu.subPages[0];
        setSelectedSubMenu(firstSubMenu);
        navigate(`/${firstSubMenu.routingUrl}`);
      }
    }
  }, [user]);
  return (
    // <div>
    //   <div>Navbar</div>
    //   {loggedInUserData.menusData &&
    //     loggedInUserData.menusData.map((menu ,ind) => {
    //       return (
    //         <div
    //           key={ind}
    //           style={{cursor:"pointer"}}
    //           onClick={() => setSelectedMenu(menu)}
    //         >
    //           {menu.menuName}
    //         </div>
    //       );
    //     })}
    //   {selectedMenu.subPages &&
    //     selectedMenu.subPages.map((page,ind) => {
    //       return (
    //         <div className="ms-3" key={ind} onClick={() => setSelectedSubMenu(page.pageName),
    //             navigate(page.routingUrl)}>
    //           <span>{page.pageName}</span>
    //         </div>
    //       )
    //     })}

    //   <Outlet />
    // </div>

    <div
      style={{
        display: "flex",
        height: "100vh",
        background: "#f4f7fc",
        fontFamily: "Arial, sans-serif",
      }}
    >
      {/* Sidebar */}
      <div
        style={{
          width: "250px",
          background: "linear-gradient(180deg,#0f172a,#1e293b)",
          color: "white",
          boxShadow: "3px 0 10px rgba(0,0,0,0.2)",
        }}
      >
        <h3
          onClick={() => handleLogOut()}
          style={{
            padding: "20px",
            textAlign: "center",
            borderBottom: "1px solid rgba(255,255,255,0.15)",
            fontWeight: "bold",
            cursor: "pointer",
            textDecoration: "underline",
          }}
        >
          EMS
        </h3>

        {menus.map((menu, ind) => (
          <div
            key={ind}
            onClick={() => handleSubMenuSelectionOnMenunChange(menu)}
            style={{
              display: "flex",
              alignItems: "center",
              gap: "12px",
              padding: "14px 20px",
              margin: "8px 12px",
              borderRadius: "10px",
              cursor: "pointer",
              transition: "0.3s",
              background:
                selectedMenu?.menuName === menu.menuName
                  ? "#3b82f6"
                  : "transparent",
              color: "#fff",
              fontWeight:
                selectedMenu?.menuName === menu.menuName ? "600" : "400",
            }}
          >
            <span className="material-icons">{menu.icon}</span>
            <span>{menu.menuName}</span>
          </div>
        ))}
      </div>

      {/* Main Content */}
      <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
        {/* Top Navigation */}
        <div
          style={{
            background: "white",
            padding: "10px",
            display: "flex",
            gap: "15px",
            boxShadow: "0 2px 8px rgba(0,0,0,0.08)",
          }}
        >
          {selectedMenu?.subPages?.map((page, ind) => (
            <div
              key={ind}
              onClick={() => {
                setSelectedSubMenu(page);
                navigate(page.routingUrl);
              }}
              style={{
                padding: "10px 18px",
                borderRadius: "10px",
                cursor: "pointer",
                transition: "0.3s",
                background:
                  selectedSubMenu?.pageName === page?.pageName
                    ? "#06b6d4"
                    : "#eef2ff",
                color:
                  selectedSubMenu?.pageName === page?.pageName
                    ? "white"
                    : "#1e293b",
                fontWeight: "600",
              }}
            >
              {page.pageName}
            </div>
          ))}
        </div>

        {/* Content */}
        <div
          style={{
            flex: 1,
            padding: "25px",
            background: "#f8fafc",
          }}
        >
          <Outlet />
        </div>
      </div>
    </div>
  );
}

export default NavbarPage;
