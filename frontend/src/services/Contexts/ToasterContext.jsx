import { useContext, useState } from "react";
import { createContext } from "react";
import { Toast } from "react-bootstrap";

export const ToastContext = createContext();

export const ToastProvider = ({ children }) => {
  const [toast, setToast] = useState({
    show: false,
    message: "",
    type: "info",
  });

  const showToast = (message, type = "info") => {
    setToast({
      show: true,
      message,
      type,
    });
  };

  const hideToast = () => {
    setToast({
      ...toast,
      show: false,
    });
  };
  const renderIcon = () => {
    switch (toast.type) {
      case "success":
        return <span className="material-icons">task_alt</span>;

      case "danger":
        return <span className="material-icons">error</span>;

      case "info":
        return <span className="material-icons">info</span>;

      default:
        return null;
    }
  };
  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      {toast.show && (
        <Toast
          show={toast.show}
          style={{
            position: "fixed",
            top: 20,
            right: 20,
            zIndex: 9999,
            height: "auto",
          }}
          onClose={hideToast}
          autohide
          delay={3000}
          className={`${toast.type}-toast`}
        >
          <div className="d-flex align-items-center">
            <span className="ms-1"> {renderIcon()}</span>
            <Toast.Body style={{ width: "100%", textOverflow: "ellipsis" }}>
              {toast.message || "Something happend."}
            </Toast.Body>
          </div>
        </Toast>
      )}
    </ToastContext.Provider>
  );
};
export const useToast = () => {
  return useContext(ToastContext);
};
