const TransactionItem = ({ tx, isUserInvolved, onSettle }) => {
    return (
        <li
            key={tx.id}
            className="p-4 flex justify-between items-center bg-[#FFD53B] text-[#1F0600] rounded-md shadow-md border-t-2 border-l-2 border-b-4 border-r-4 border-[#000]"
        >
            <div className="flex flex-col">
                <span className="text-lg font-bold leading-tight">
                    <span className="text-[#030C03]">{tx.payerName}</span>{" "}
                    <span className="text-[#306B34] font-extrabold">paid</span>{" "}
                    <span className="text-[#030303]">₹{tx.amount}</span>{" "}
                    <span className="text-[#030C03]">on behalf of</span>{" "}
                    <span className="text-[#030303] font-bold">{tx.payeeName}</span>
                </span>

                {/* Description with normal text styling */}
                <span className="mt-1 text-sm text-[#030C03] italic font-medium">
                    {tx.description}
                </span>

                <span className={`text-sm ${tx.settled ? "text-green-500 font-bold" : "text-[#A31621] font-semibold"} mt-1`}>
                    Settle Status: {tx.settled ? "Settled" : "Pending"}
                </span>
            </div>

            {!tx.settled && isUserInvolved && (
                <button
                    onClick={() => onSettle(tx.id)}
                    className="bg-[#FCF5E5] border-l-2 border-t-2 border-r-4 border-b-4 border-[#030303] text-black px-4 py-2 rounded-md text-sm font-semibold shadow-sm hover:brightness-110 transition-all duration-300"
                >
                    Settle
                </button>
            )}
        </li>
    );
};

export default TransactionItem;
