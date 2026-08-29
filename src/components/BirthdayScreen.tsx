import React, { useState } from 'react';
import { ScreenType, PartnerProfile, WishlistItem } from '../types';

interface BirthdayScreenProps {
  partner: PartnerProfile;
  wishlist: WishlistItem[];
  onAddWishlistItem: (title: string) => void;
  onNavigate: (screen: ScreenType) => void;
}

export const BirthdayScreen: React.FC<BirthdayScreenProps> = ({
  partner,
  wishlist,
  onAddWishlistItem,
  onNavigate,
}) => {
  const [showAddWishlist, setShowAddWishlist] = useState(false);
  const [newItemTitle, setNewItemTitle] = useState('');

  const calculateDaysLeft = () => {
    try {
      const today = new Date();
      const dob = new Date(partner.dob);
      const nextBirthday = new Date(
        today.getFullYear(),
        dob.getMonth(),
        dob.getDate()
      );
      if (nextBirthday < today) {
        nextBirthday.setFullYear(today.getFullYear() + 1);
      }
      const diffTime = nextBirthday.getTime() - today.getTime();
      return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    } catch {
      return 14;
    }
  };

  const handleAddWishlist = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newItemTitle.trim()) return;
    onAddWishlistItem(newItemTitle.trim());
    setNewItemTitle('');
  };

  const daysLeft = calculateDaysLeft();

  return (
    <div className="pt-20 pb-32 px-4 sm:px-6 max-w-lg mx-auto relative z-10 space-y-6">
      {/* Partner Profile Section */}
      <section className="flex flex-col items-center text-center pt-2">
        <div className="relative w-36 h-36 mb-3">
          <div className="w-full h-full rounded-full overflow-hidden border-4 border-white shadow-xl p-0.5 bg-white relative z-10">
            <img
              src={partner.avatarUrl}
              alt={partner.name}
              className="w-full h-full rounded-full object-cover"
            />
          </div>
          {/* Floating decorative hearts */}
          <span
            className="material-symbols-outlined absolute top-2 -right-2 text-[#E60039] floating-heart z-20 text-2xl"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            favorite
          </span>
          <span
            className="material-symbols-outlined absolute bottom-4 -left-3 text-[#FF3366] floating-heart z-20 text-xl"
            style={{ animationDelay: '1s', fontVariationSettings: "'FILL' 1" }}
          >
            favorite
          </span>
        </div>

        <h2 className="font-display font-extrabold text-2xl text-[#2D2226] mb-0.5">
          {partner.name}'s Birthday
        </h2>
        <p className="font-body text-xs text-[#7A6E73] font-medium">
          Let's make their day unforgettable.
        </p>
      </section>

      {/* Countdown Card */}
      <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-6 border border-white/90 shadow-[0_12px_36px_-6px_rgba(230,0,57,0.08)] relative overflow-hidden">
        <div className="relative z-10 flex flex-col items-center text-center py-2">
          <span className="font-body text-xs text-[#E60039] tracking-widest uppercase font-bold mb-1">
            Countdown
          </span>
          <div className="font-display text-[56px] leading-none font-extrabold text-[#E60039] mb-1">
            {daysLeft}
          </div>
          <span className="font-display font-bold text-sm text-[#2D2226]">
            Days Left
          </span>
        </div>

        <div className="mt-4 border-t border-rose-100 pt-4 flex justify-between items-center px-1">
          <div className="flex items-center gap-2">
            <span
              className="material-symbols-outlined text-[#E60039]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              cake
            </span>
            <span className="font-body text-xs text-[#2D2226] font-bold">
              Oct 24th
            </span>
          </div>
          <button
            onClick={() => alert('Special birthday planner active! Added surprise date task to calendar.')}
            className="glow-button px-4 py-2 rounded-full font-display font-bold text-xs flex items-center gap-1.5 active:scale-95 transition-all cursor-pointer"
          >
            <span className="material-symbols-outlined text-[16px]">
              edit_calendar
            </span>
            <span>Plan Birthday</span>
          </button>
        </div>
      </section>

      {/* Gift Ideas Section */}
      <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-5 border border-white/90 shadow-[0_10px_30px_rgba(230,0,57,0.07)]">
        <div
          onClick={() => setShowAddWishlist(!showAddWishlist)}
          className="flex items-center gap-3.5 cursor-pointer"
        >
          <div className="w-11 h-11 rounded-2xl bg-rose-50 border border-rose-200 flex items-center justify-center text-[#E60039]">
            <span
              className="material-symbols-outlined text-2xl"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              redeem
            </span>
          </div>
          <div className="flex-1">
            <h3 className="font-display font-bold text-sm text-[#2D2226]">
              Gift Ideas Wishlist
            </h3>
            <p className="font-body text-xs text-[#7A6E73] font-medium">
              {wishlist.length} items saved
            </p>
          </div>
          <span className="material-symbols-outlined text-[#7A6E73]">
            {showAddWishlist ? 'expand_less' : 'chevron_right'}
          </span>
        </div>

        {/* Wishlist Items List & Form */}
        {showAddWishlist && (
          <div className="mt-4 pt-4 border-t border-rose-100 space-y-3">
            <div className="space-y-2">
              {wishlist.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center justify-between p-3 rounded-2xl bg-rose-50/60 border border-rose-100"
                >
                  <span className="font-body text-xs text-[#2D2226] font-semibold">
                    🎁 {item.title}
                  </span>
                  <span className="text-[10px] text-[#E60039] font-extrabold uppercase">
                    Saved
                  </span>
                </div>
              ))}
            </div>

            <form onSubmit={handleAddWishlist} className="flex gap-2 pt-2">
              <input
                type="text"
                value={newItemTitle}
                onChange={(e) => setNewItemTitle(e.target.value)}
                placeholder="Add gift idea (e.g. Silk Scarf)..."
                className="flex-1 bg-white border border-rose-200/80 px-4 py-2.5 rounded-2xl text-xs text-[#2D2226] focus:border-[#E60039] outline-none font-body"
              />
              <button
                type="submit"
                className="glow-button px-4 py-2.5 rounded-2xl font-display font-bold text-xs whitespace-nowrap cursor-pointer active:scale-95"
              >
                Add
              </button>
            </form>
          </div>
        )}
      </section>
    </div>
  );
};
