import { Route, BrowserRouter as Router, Routes } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import ProtectedRoute from "./components/auth/ProtectedRoute";
import Dashboard from "./pages/Dashboard";
import CreateAccountPage from "./pages/CreateAccountPage";
import AccountList from "./components/account/AccountList";
import AccountDetails from "./components/account/AccountDetails";
import CreateTransactionForm from "./components/txn/CreateTransactionForm";

function App() {
  return (
    <Router>
      <div className="container mt-4">
        <Routes>
          <Route path="/" element={<Home />}/>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/dashboard"
            element={
                <ProtectedRoute>
                    <Dashboard />
                </ProtectedRoute>
            }
          />
          <Route
            path="/apply-to-account"
            element={
              <ProtectedRoute>
                  <CreateAccountPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/accounts"
            element={
              <ProtectedRoute>
                  <AccountList />
              </ProtectedRoute>
            }/>
          <Route
            path="/accounts/:accountNumber"
            element={
              <ProtectedRoute>
                  <AccountDetails />
              </ProtectedRoute>
            }
          />
            <Route
                path="/create-transaction"
                element={
                <ProtectedRoute>
                    <CreateTransactionForm />
                </ProtectedRoute>
                }
            />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
