import React, { useState } from 'react';
import { ScreenType } from '../types';

interface UpdateStatusScreenProps {
  currentStatus: string;
  onUpdateStatus: (newStatus: string) => void;
  onNavigate: (screen: ScreenType) => void;
}

const QUICK_STATUSES = [
  { label: 'Reached Home', icon: 'home' },
  { label: 'Busy', icon: 'work' },
  { label: 'Sleeping', icon: 'bed' },
  { label: 'Driving', icon: 'directions_car' },
  { label: 'Travelling', icon: 'flight' },
  { label: 'Eating', icon: 'restaurant' },
  { label: 'Shopping', icon: 'shopping_bag' },
];

export const UpdateStatusScreen: React.FC<UpdateStatusScreenProps> = ({
  currentStatus,
  onUpdateStatus,
  onNavigate,
}) => {
  const [selectedStatus, setSelectedStatus] = useState(currentStatus);
  const [customStatusInput, setCustomStatusInput] = useState('');
  const [lastUpdated, setLastUpdated] = useState('Just now');

  const handleSelectStatus = (label: string) => {
    setSelectedStatus(label);
    onUpdateStatus(label);
    setLastUpdated('Just now');
  };

  const handleSetCustomStatus = (e: React.FormEvent) => {
    e.preventDefault();
    if (!customStatusInput.trim()) return;
    setSelectedStatus(customStatusInput.trim());
    onUpdateStatus(customStatusInput.trim());
    setCustomStatusInput('');
    setLastUpdated('Just now');
  };

  return (
    <div className="pt-20 pb-32 px-4 sm:px-6 max-w-lg mx-auto space-y-6 relative z-10">
      {/* Header Section */}
      <section className="text-center pt-2">
        <h1 className="font-display font-extrabold text-2xl text-[#2D2226] mb-1">
          Update Status
        </h1>
        <p className="font-body text-xs text-[#7A6E73] font-medium">
          Let your partner know what you're up to in real time.
        </p>
      </section>

      {/* Current Status Display */}
      <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-6 flex flex-col items-center justify-center text-center border border-white/90 shadow-[0_10px_30px_rgba(230,0,57,0.07)]">
        <div className="w-16 h-16 rounded-2xl bg-rose-50 border border-rose-200 flex items-center justify-center mb-3 text-[#E60039] shadow-sm">
          <span
            className="material-symbols-outlined text-3xl"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            work
          </span>
        </div>
        <h2 className="font-display font-bold text-lg text-[#E60039] mb-0.5">
          Currently {selectedStatus}
        </h2>
        <p className="font-body text-xs text-[#7A6E73] font-medium">
          Updated {lastUpdated}
        </p>
      </section>

      {/* Quick Status Grid */}
      <section>
        <h3 className="font-display font-bold text-sm text-[#2D2226] mb-3">
          Quick Select
        </h3>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          {QUICK_STATUSES.map((item) => {
            const isActive = selectedStatus.toLowerCase() === item.label.toLowerCase();
            return (
              <button
                key={item.label}
                onClick={() => handleSelectStatus(item.label)}
                className={`bg-white/90 border rounded-2xl p-3.5 flex flex-col items-center justify-center gap-1.5 transition-all shadow-xs active:scale-95 cursor-pointer ${
                  isActive
                    ? 'bg-rose-50 border-[#E60039] shadow-sm text-[#E60039]'
                    : 'border-white/90 hover:bg-rose-50/50 text-[#2D2226]'
                }`}
              >
                <span
                  className={`material-symbols-outlined text-2xl ${
                    isActive ? 'text-[#E60039]' : 'text-[#7A6E73]'
                  }`}
                  style={{ fontVariationSettings: isActive ? "'FILL' 1" : "'FILL' 0" }}
                >
                  {item.icon}
                </span>
                <span
                  className={`font-body text-xs ${
                    isActive ? 'text-[#E60039] font-bold' : 'text-[#2D2226] font-medium'
                  }`}
                >
                  {item.label}
                </span>
              </button>
            );
          })}
        </div>
      </section>

      {/* Custom Status */}
      <section className="bg-white/90 backdrop-blur-2xl rounded-3xl p-6 border border-white/90 shadow-[0_10px_30px_rgba(230,0,57,0.07)]">
        <h3 className="font-display font-bold text-sm text-[#2D2226] mb-3">
          Custom Status
        </h3>
        <form onSubmit={handleSetCustomStatus} className="flex items-center gap-3">
          <input
            type="text"
            value={customStatusInput}
            onChange={(e) => setCustomStatusInput(e.target.value)}
            placeholder="What's on your mind?"
            className="flex-1 bg-white border border-rose-200/80 px-4 py-3 text-sm text-[#2D2226] rounded-2xl focus:border-[#E60039] outline-none"
          />
          <button
            type="submit"
            className="glow-button px-5 py-3 rounded-2xl font-display font-bold text-xs whitespace-nowrap cursor-pointer active:scale-95"
          >
            Update
          </button>
        </form>
      </section>
    </div>
  );
};
