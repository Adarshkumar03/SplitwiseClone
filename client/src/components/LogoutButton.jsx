import { useNavigate } from "react-router";
import api from "../utils/api"
import useAuthStore from "../store/authStore";

const LogoutButton = () => {
  const navigate = useNavigate();
  const handleLogout = async () => {
        await api.post("/auth/logout");
        useAuthStore.getState().logout();
        navigate("/login");
  }
  
  return (
    <button onClick={handleLogout} className="bg-[#A31621] transition-all duration-500 hover:brightness-125 border-l-2 border-t-2 border-r-4 border-b-4 border-[#030303] text-[#fbfbfb] p-2 rounded-md font-semibold">Logout</button>
  )
}

export default LogoutButton