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
    <div className="pt-24 pb-32 px-6 max-w-lg mx-auto relative z-10">
      {/* Partner Profile Section */}
      <section className="flex flex-col items-center mb-8 relative">
        <div className="relative w-44 h-48 mb-4">
          {/* Glowing Aura */}
          <div className="absolute inset-0 rounded-full bg-[#ffcbd5]/20 blur-2xl animate-pulse" />
          <img
            src={partner.avatarUrl}
            alt={partner.name}
            className="w-full h-full rounded-full object-cover border-2 border-white/20 shadow-[0_0_40px_rgba(244,167,185,0.3)] relative z-10"
          />
          {/* Floating decorative hearts */}
          <span
            className="material-symbols-outlined absolute top-4 -right-2 text-[#ffcbd5] floating-heart z-20 text-2xl drop-shadow-[0_0_8px_rgba(244,167,185,0.8)]"
            data-weight="fill"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            favorite
          </span>
          <span
            className="material-symbols-outlined absolute bottom-6 -left-4 text-[#ffcbcf] floating-heart z-20 text-xl drop-shadow-[0_0_8px_rgba(255,203,207,0.8)]"
            style={{ animationDelay: '1s', fontVariationSettings: "'FILL' 1" }}
          >
            favorite
          </span>
        </div>

        <h2 className="font-display font-bold text-2xl md:text-3xl text-[#dae2fd] mb-1">
          {partner.name}'s Birthday
        </h2>
        <p className="font-body text-base text-[#d6c1c5] text-center">
          Let's make it unforgettable.
        </p>
      </section>

      {/* Countdown Card */}
      <section className="glass-card rounded-[24px] p-6 mb-6 relative overflow-hidden glow-effect shadow-[0_8px_32px_0_rgba(0,0,0,0.37)] bg-white/10 border border-white/20">
        <div className="relative z-10 flex flex-col items-center text-center py-4">
          <span className="font-body text-xs text-[#ffcbd5] tracking-widest uppercase font-semibold mb-2">
            Countdown
          </span>
          <div className="font-display text-[64px] leading-none font-bold bg-gradient-to-b from-white to-[#ffcbd5] bg-clip-text text-transparent drop-shadow-[0_4px_12px_rgba(255,203,213,0.3)] mb-2">
            {daysLeft}
          </div>
          <span className="font-display text-lg text-[#d6c1c5]">
            Days Left
          </span>
        </div>

        <div className="mt-4 border-t border-white/10 pt-4 flex justify-between items-center px-2">
          <div className="flex items-center gap-2">
            <span
              className="material-symbols-outlined text-[#ffcbcf]"
              data-weight="fill"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              cake
            </span>
            <span className="font-body text-base text-[#dae2fd] font-medium">
              Oct 24th
            </span>
          </div>
          <button
            onClick={() => alert('Special birthday planner active! Added surprise date task to calendar.')}
            className="bg-[#ffcbd5]/20 hover:bg-[#ffcbd5]/30 border border-[#ffcbd5]/30 px-4 py-2 rounded-full font-body text-xs font-semibold text-[#ffcbd5] transition-colors flex items-center gap-2 backdrop-blur-xl active:scale-95 cursor-pointer"
          >
            <span className="material-symbols-outlined text-[16px]">
              edit_calendar
            </span>
            Plan
          </button>
        </div>
      </section>

      {/* Gift Ideas Section */}
      <section className="glass-card rounded-2xl p-5 shadow-[0_8px_32px_0_rgba(0,0,0,0.37)] bg-white/10 border border-white/20">
        <div
          onClick={() => setShowAddWishlist(!showAddWishlist)}
          className="flex items-center gap-4 cursor-pointer"
        >
          <div className="w-12 h-12 rounded-full bg-[#593d5f]/50 flex items-center justify-center border border-[#fcd7ff]/20">
            <span
              className="material-symbols-outlined text-[#dfbbe4] text-2xl"
              data-weight="fill"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              redeem
            </span>
          </div>
          <div className="flex-1">
            <h3 className="font-display font-medium text-lg text-[#dae2fd]">
              Gift Ideas Wishlist
            </h3>
            <p className="font-body text-xs text-[#d6c1c5]">
              {wishlist.length} items saved
            </p>
          </div>
          <span className="material-symbols-outlined text-[#d6c1c5]">
            {showAddWishlist ? 'expand_less' : 'chevron_right'}
          </span>
        </div>

        {/* Wishlist Items List & Form */}
        {showAddWishlist && (
          <div className="mt-4 pt-4 border-t border-white/10 space-y-3">
            <div className="space-y-2">
              {wishlist.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center justify-between p-3 rounded-xl bg-white/5 border border-white/10"
                >
                  <span className="font-body text-sm text-[#dae2fd]">
                    🎁 {item.title}
                  </span>
                  <span className="text-xs text-[#ffcbd5] font-semibold">
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
                className="glass-input flex-1 py-2.5 px-4 rounded-xl text-sm font-body bg-white/5"
              />
              <button
                type="submit"
                className="bg-[#ffcbd5] text-[#521f2e] px-4 py-2.5 rounded-xl font-body text-xs font-bold hover:bg-[#ffd9e0] transition-colors"
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
