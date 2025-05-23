import { useState } from "react";
import { useNavigate } from "react-router";
import api from "../utils/api";
import { toast } from "react-toastify";
import UserNavbar from "./UserNavbar";

export default function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const validateForm = () => {
    const nameRegex = /^[a-zA-Z\s]{2,50}$/;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;

    if (!nameRegex.test(name)) {
      setError("Name must be 2-50 characters and contain only letters and spaces.");
      return false;
    }

    if (!emailRegex.test(email)) {
      setError("Please enter a valid email address.");
      return false;
    }

    if (!passwordRegex.test(password)) {
      setError(
        "Password must be at least 8 characters, include uppercase, lowercase, number, and special character."
      );
      return false;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return false;
    }

    setError("");
    return true;
  };

  const handleRegister = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    try {
      await api.post("/auth/register", { name, email, password });
      toast.success("User registered successfully!");
      navigate("/login");
    } catch {
      setError("Error registering user. Try again.");
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <UserNavbar />

      <main className="flex items-center justify-center flex-grow px-5 bg-gradient-to-b from-[#AAD7B8] via-[#909CC2] to-[#306B34]">
        <section className="bg-[#909CC2] border-l-2 border-t-2 border-r-5 border-b-5 border-[#030303] p-8 sm:p-10 rounded-lg shadow-lg w-full max-w-md sm:w-96 md:w-[28rem]">
          <header className="text-center mb-6">
            <h2 className="text-4xl font-bold text-[#030C03] font-bebas tracking-wider">
              Register
            </h2>
          </header>

          {error && <p className="text-red-600 text-sm text-center font-semibold">{error}</p>}

          <form onSubmit={handleRegister} className="space-y-4">
            
            <article>
              <label className="block text-[#030C03] font-semibold">
                Name <span className="text-red-600">*</span>
              </label>
              <input
                type="text"
                placeholder="Enter your name"
                className="w-full p-3 bg-white border border-gray-400 rounded-md focus:outline-none focus:ring-2 focus:ring-[#306B34]"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </article>

            <article>
              <label className="block text-[#030C03] font-semibold">
                Email <span className="text-red-600">*</span>
              </label>
              <input
                type="email"
                placeholder="Enter your email"
                className="w-full p-3 bg-white border border-gray-400 rounded-md focus:outline-none focus:ring-2 focus:ring-[#306B34]"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </article>

            <article>
              <label className="block text-[#030C03] font-semibold">
                Password <span className="text-red-600">*</span>
              </label>
              <input
                type="password"
                placeholder="Enter password"
                className="w-full p-3 bg-white border border-gray-400 rounded-md focus:outline-none focus:ring-2 focus:ring-[#306B34]"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </article>

            <article>
              <label className="block text-[#030C03] font-semibold">
                Confirm Password <span className="text-red-600">*</span>
              </label>
              <input
                type="password"
                placeholder="Confirm password"
                className={`w-full p-3 bg-white border rounded-md focus:outline-none ${
                  error.includes("Passwords") ? "border-red-500 focus:ring-red-500" : "border-gray-400 focus:ring-[#306B34]"
                }`}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </article>

            <button
              className="w-full bg-[#306B34] border-l-2 border-t-2 border-r-4 border-b-4 border-[#030303] text-white py-2 rounded-md font-semibold hover:bg-[#245824] transition-all duration-300"
            >
              Register
            </button>
          </form>

          {/* Login link */}
          <footer className="text-center text-sm mt-4 font-medium">
            Already have an account?{" "}
            <span
              onClick={() => navigate("/login")}
              className="text-[#306B34] cursor-pointer hover:underline font-semibold"
            >
              Login
            </span>
          </footer>
        </section>
      </main>
    </div>
  );
}
