import TransactionHistory from "./TransactionHistory";

const Dashboard = () => {
    return (
        <div>
            <h2 className="text-2xl font-bold">Dashboard</h2>
            <TransactionHistory transactions={[]} />
        </div>
    );
};

export default Dashboard;
