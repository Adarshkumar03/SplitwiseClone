import UserTransactionList from "./UserTransactionList";

const Dashboard = () => {
    return (
        <div className="p-4 sm:p-6 md:p-8 lg:p-10 xl:p-12 w-full max-w-6xl mx-auto">
            <h2 className="text-3xl sm:text-4xl font-bold text-[#030303] font-bebas tracking-wide text-center sm:text-left">
                Dashboard
            </h2>
            
            <section className="mt-4 sm:mt-6">
                <h3 className="text-xl sm:text-2xl font-semibold text-[#030C03] text-center sm:text-left">
                    Transaction History
                </h3>
                <UserTransactionList />
            </section>
        </div>
    );
};

export default Dashboard;