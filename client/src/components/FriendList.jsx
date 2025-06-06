import { MdPersonAddAlt1 } from "react-icons/md";

const FriendList = ({
  friends,
  selectedFriend,
  onSelectFriend,
  onAddFriend,
}) => {
  return (
    <div>
      <ul className="space-y-2">
        {friends.length === 0 ? (
          <p className="font-semibold text-center text-[#030303] mb-4">
            No friends available
          </p>
        ) : null}
        {friends.map((friend) => (
          <li
            key={friend.id}
            onClick={() => onSelectFriend(friend)}
            className={`p-3 rounded-md cursor-pointer transition-all duration-300 border-3 border-[#030C03] text-[#030C03] font-semibold 
                             ${selectedFriend?.id === friend.id 
                    ? "bg-[#030C03] text-white font-bold border-[#030C03]" 
                    : "bg-[#AAD7B8] hover:bg-[#030C03] hover:text-white"
                  }`}
          >
            {friend.name}
          </li>
        ))}
      </ul>
      <button
        onClick={onAddFriend}
        className="mt-4 w-full bg-[#342256] border-l-2 border-t-2 border-r-4 border-b-4 border-[#030303] text-white font-bold py-2 rounded-md shadow-md hover:bg-[#281b43] transition duration-300"
      >
        <MdPersonAddAlt1 className="inline mr-2 text-2xl md:text-xl" />
        Add Friend
      </button>
    </div>
  );
};

export default FriendList;
