import { useEffect, useState } from "react";
import api from "../utils/api";
import useAuthStore from "../store/authStore"; // Assuming Zustand for auth
import { toast } from "react-toastify";
import TransactionComponent from "./TransactionComponent";

const UserTransactionList = () => {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const { user } = useAuthStore(); // Get logged-in user

    useEffect(() => {
        fetchUserTransactions(user.id);
    }, [user]);

    const fetchUserTransactions = async (userId) => {
        try {
            const response = await api.get(`/transactions/users/${userId}`);
            setTransactions(response.data);
        } catch (error) {
            console.error("Error fetching transactions:", error);
        }
        setLoading(false);
    };

    const handleSettleTransaction = async (transactionId) => {
        try {
            await api.put(`/transactions/${transactionId}/settle`);
            fetchUserTransactions(user.id);
            toast.success("Transaction settled!!");
        } catch (error) {
            console.error("Error settling transaction:", error);
        }
    };

    if (loading) return <p className="text-center text-gray-400">Loading transactions...</p>;

    return <TransactionComponent transactions={transactions} loading={loading} user={user} onSettle={handleSettleTransaction} />;
};

export default UserTransactionList;
