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
    <div className="pt-24 pb-32 px-6 max-w-4xl mx-auto space-y-8 relative z-10">
      {/* Header Section */}
      <section className="text-center pt-2">
        <h1 className="font-display font-bold text-2xl md:text-3xl text-[#dae2fd] mb-1">
          Update Status
        </h1>
        <p className="font-body text-sm text-[#d6c1c5]">
          Let them know what you're up to.
        </p>
      </section>

      {/* Current Status Display */}
      <section className="glass-card rounded-2xl p-6 flex flex-col items-center justify-center text-center shadow-[0_8px_32px_0_rgba(0,0,0,0.37)] bg-white/10">
        <div className="w-20 h-20 rounded-full bg-[#ffcbd5]/15 border border-[#ffcbd5]/30 flex items-center justify-center mb-4 shadow-[0_0_20px_rgba(244,167,185,0.3)]">
          <span
            className="material-symbols-outlined text-4xl text-[#ffcbd5]"
            data-weight="fill"
            style={{ fontVariationSettings: "'FILL' 1" }}
          >
            work
          </span>
        </div>
        <h2 className="font-display font-medium text-xl text-[#ffcbd5] mb-1">
          Currently {selectedStatus}
        </h2>
        <p className="font-body text-sm text-[#d6c1c5]">
          Updated {lastUpdated}
        </p>
      </section>

      {/* Quick Status Grid */}
      <section>
        <h3 className="font-display font-medium text-lg mb-4 text-[#dae2fd]">
          Quick Select
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {QUICK_STATUSES.map((item) => {
            const isActive = selectedStatus.toLowerCase() === item.label.toLowerCase();
            return (
              <button
                key={item.label}
                onClick={() => handleSelectStatus(item.label)}
                className={`status-btn glass-card rounded-xl p-4 flex flex-col items-center justify-center gap-2 transition-all shadow-md active:scale-95 cursor-pointer ${
                  isActive
                    ? 'active bg-[#f4a7b9]/20 border-[#f4a7b9] shadow-[0_0_15px_rgba(244,167,185,0.3)]'
                    : 'hover:bg-white/10'
                }`}
              >
                <span
                  className={`material-symbols-outlined text-3xl ${
                    isActive ? 'text-[#ffcbd5]' : 'text-[#dae2fd]'
                  }`}
                  data-weight={isActive ? 'fill' : 'none'}
                  style={{ fontVariationSettings: isActive ? "'FILL' 1" : "'FILL' 0" }}
                >
                  {item.icon}
                </span>
                <span
                  className={`font-body text-sm ${
                    isActive ? 'text-[#ffcbd5] font-semibold' : 'text-[#dae2fd]'
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
      <section className="glass-card rounded-2xl p-6 shadow-[0_8px_32px_0_rgba(0,0,0,0.37)] bg-white/10">
        <h3 className="font-display font-medium text-lg mb-4 text-[#dae2fd]">
          Custom Status
        </h3>
        <form onSubmit={handleSetCustomStatus} className="flex items-end gap-4">
          <div className="flex-1">
            <input
              type="text"
              value={customStatusInput}
              onChange={(e) => setCustomStatusInput(e.target.value)}
              placeholder="What's on your mind?"
              className="glass-input w-full pb-2 text-[#dae2fd] font-body text-base placeholder-white/30 bg-transparent border-0 border-b border-white/20 focus:ring-0 focus:border-[#ffcbd5]"
            />
          </div>
          <button
            type="submit"
            className="bg-[#ffcbd5] text-[#521f2e] px-6 py-2.5 rounded-full font-body text-xs font-bold uppercase tracking-wider hover:bg-[#ffd9e0] transition-opacity shadow-[0_0_15px_rgba(244,167,185,0.4)] cursor-pointer"
          >
            Set
          </button>
        </form>
      </section>
    </div>
  );
};
