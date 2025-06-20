import { useEffect, useState } from "react";
import { useOutletContext, useNavigate } from "react-router";
import AddUserModal from "./modals/AddUserModal";
import AddExpenseModal from "./modals/AddExpenseModal";
import OweDetailsModal from "./modals/OweDetailsModal";
import TransactionList from "./TransactionList";
import GroupHeader from "./GroupHeader";
import GroupMembersList from "./GroupMembersList";
import useTransactionStore from "../store/transactionStore";
import api from "../utils/api";
import { toast } from "react-toastify";
import ConfirmModal from "./modals/ConfirmModal";
import UpdateExpenseModal from "./modals/UpdateExpenseModal";

const GroupDashboard = () => {
  const { selectedGroup, fetchGroups, refreshTx, setRefreshTx } = useOutletContext();
  const [userModalOpen, setUserModalOpen] = useState(false);
  const [expenseModalOpen, setExpenseModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [oweModalOpen, setOweModalOpen] = useState(false);
  const [confirmLeaveOpen, setConfirmLeaveOpen] = useState(false);
  const [editTx, setEditTx] = useState(null);
  const navigate = useNavigate();

  const { groupDetails, fetchGroupDetails, updateTransaction } = useTransactionStore();

  useEffect(() => {
    if (selectedGroup?.id) {
      fetchGroupDetails(selectedGroup.id);
    }
  }, [selectedGroup, fetchGroupDetails]);

  const handleSettleTransaction = async (transactionId) => {
    try {
      await api.put(`/transactions/${transactionId}/settle`);
      toast.success("Transaction settled!");
      setRefreshTx((prev) => !prev);
    } catch (error) {
      console.error("Error settling transaction:", error);
      toast.error("Failed to settle transaction");
    }
  };

  if (!selectedGroup)
    return <p className="text-center text-gray-400">No group selected</p>;

  return (
    <main className="w-full px-4 md:px-6 pt-0 pb-4 md:pb-6 bg-[#FFF8ED]">
      {/* Group Header */}
      <div className="bg-[#FFB759] p-4 md:p-6 rounded-md shadow-2xl border-t-3 border-l-3 border-r-5 border-b-5 border-[#000] mb-6">
        <GroupHeader
          groupName={selectedGroup.name}
          onOpenExpenseModal={() => setExpenseModalOpen(true)}
          setConfirmLeaveOpen={setConfirmLeaveOpen}
        />
      </div>

      {/* Responsive Layout */}
      <div className="flex flex-col gap-6 md:grid md:grid-cols-3">

        {/* Transactions Section */}
        <article className="md:col-span-2 bg-[#FFF7EC] p-4 md:p-6 rounded-md shadow-2xl border-t-3 border-l-3 border-r-5 border-b-5 border-[#000]">
          <section>
            <h3 className="text-2xl font-bold text-[#040404] text-center mb-4 font-sora">
              Group Transactions
            </h3>
            <TransactionList
              groupId={selectedGroup.id}
              onSettle={handleSettleTransaction}
              onEdit={(tx) => setEditTx(tx)}
              refreshTx={refreshTx}
              setRefreshTx={setRefreshTx}
            />
          </section>
        </article>

        {/* Group Members Section */}
        <aside>
          <GroupMembersList
            groupDetails={groupDetails}
            onOpenOweDetails={setSelectedUser}
            setOweModalOpen={setOweModalOpen}
            onOpenUserModal={() => setUserModalOpen(true)}
            className="w-full"
          />
        </aside>
      </div>

      {/* Modals */}
      {userModalOpen && (
        <AddUserModal
          groupId={selectedGroup.id}
          onClose={() => setUserModalOpen(false)}
          refreshGroupDetails={() => fetchGroupDetails(selectedGroup.id)}
        />
      )}
      {oweModalOpen && (
        <OweDetailsModal
          user={selectedUser}
          groupId={selectedGroup.id}
          onClose={() => setOweModalOpen(false)}
        />
      )}
      {expenseModalOpen && (
        <AddExpenseModal
          groupId={selectedGroup.id}
          groupName={selectedGroup.name}
          groupMembers={groupDetails?.users || []}
          onClose={() => {
            setExpenseModalOpen(false);
            setRefreshTx((prev) => !prev);
          }}
        />
      )}
      {editTx && (
        <UpdateExpenseModal
          open={!!editTx}
          transaction={editTx}
          onClose={() => setEditTx(null)}
          onUpdate={(updatedTx) => {
            updateTransaction(updatedTx, false);
            setEditTx(null);
          }}
        />
      )}
      {confirmLeaveOpen && (
        <ConfirmModal
          open={confirmLeaveOpen}
          title="Leave Group"
          message={`Are you sure you want to leave the group "${selectedGroup.name}"?`}
          onCancel={() => setConfirmLeaveOpen(false)}
          onConfirm={async () => {
            try {
              await api.delete(`/groups/${selectedGroup.id}/leave`);
              toast.success("Left group successfully");
              await fetchGroups();
              setConfirmLeaveOpen(false);
              navigate("/dashboard");
            } catch {
              toast.error("Error leaving group");
            }
          }}
        />
      )}
    </main>
  );
};

export default GroupDashboard;
