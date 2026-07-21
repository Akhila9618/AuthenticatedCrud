import React, { useEffect, useState } from "react";
import {
  Button,
  Card,
  Col,
  Container,
  Form,
  InputGroup,
  Row,
} from "react-bootstrap";
import { generateToken, login } from "../services/apiservice";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../services/Contexts/AuthContext";
import { useToast } from "../services/Contexts/ToasterContext";

function Login() {
  const navigation = useNavigate();
  // renaming while destructuring
  const { login: loginAuth } = useAuth();
  const { showToast, hideToast } = useToast();
  const [showPassword, setShowPassword] = useState(false);
  const handleLogin = async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const payload = Object.fromEntries(formData);
    try {
      const loginResponse = await login(payload);
      if (loginResponse && loginResponse.status === "success") {
        tokenGeneration(payload , loginResponse.data);
      } else {
        showToast(
          loginResponse.message || "An Error Occured during login",
          "error",
        );
      }
    } catch (error) {
      showToast(error.message || "An Error Occured during login", "error");
    }
  };
  const tokenGeneration = async (userCredentials , loginResponse) => {
    try {
      const res = await generateToken(userCredentials);
      if (res) {
        const updatedResponse = {...loginResponse , token :res}
        loginAuth(updatedResponse);
        navigation("/");
      } else {
        showToast("Error while generating token");
      }
    } catch (error) {
       showToast(error.message|| error.msg || "An Error Occured during login", "error");
    }
  };
  

  return (
    // <div className="login-container">
    //   <h5>Login Form</h5>
    //   <Form onSubmit={(e) => handleLogin(e)}>
    //     <Form.Group className="mb-3" controlId="formGroupEmail">
    //       <Form.Label>Email address</Form.Label>
    //       <Form.Control
    //         type="text"
    //         placeholder="Enter usename"
    //         name="userName"
    //       />
    //     </Form.Group>
    //     <Form.Group className="mb-3" controlId="formGroupPassword">
    //       <Form.Label>Password</Form.Label>
    //       <Form.Control
    //         type="password"
    //         placeholder="Password"
    //         name="password"
    //       />
    //     </Form.Group>
    //     <Button variant="primary" type="submit">
    //       submit
    //     </Button>
    //   </Form>
    // </div>
    <Container fluid className="login-page">
      <Row className="justify-content-center align-items-center min-vh-100">
        <Col xs={11} sm={9} md={7} lg={5} xl={4}>
          <Card className="shadow-sm border-0 rounded-4">
            <Card.Body className="p-5">
              <div className="text-center mb-4">
                <h2 className="fw-bold text-prim5ary">Welcome Back</h2>
                <p className="text-muted mb-0">Sign in to continue</p>
              </div>

              <Form onSubmit={handleLogin}>
                <Form.Group className="mb-3">
                  <Form.Label>Username</Form.Label>
                  <Form.Control
                    type="text"
                    name="userName"
                    placeholder="Enter username"
                    required
                  />
                </Form.Group>

                {/* <Form.Group className="mb-4">
                  <Form.Label>Password</Form.Label>
                  <Form.Control
                    type="password"
                    name="password"
                    placeholder="Enter password"
                    required
                  />
                </Form.Group> */}
                <Form.Label>Password</Form.Label>
                <InputGroup className="mb-4">
                  <Form.Control
                    type={showPassword ? "text" : "password"}
                    name="password"
                    placeholder="Enter password"
                    required
                  />
                  <InputGroup.Text>
                    <span
                      className="material-icons"
                      style={{ cursor: "pointer" }}
                      onMouseEnter={() => setShowPassword(true)}
                      onMouseLeave={() => setShowPassword(false)}
                    >
                      {showPassword ? "visibility_off" : "visibility"}
                    </span>
                  </InputGroup.Text>
                </InputGroup>

                <div className="d-grid">
                  <Button
                    variant="primary"
                    size="lg"
                    type="submit"
                    className="rounded-pill"
                  >
                    Login
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default Login;
