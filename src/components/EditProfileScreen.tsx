import React, { useState } from 'react';
import { ScreenType, PartnerProfile } from '../types';

interface EditProfileScreenProps {
  partner: PartnerProfile;
  userNickname: string;
  onSaveProfile: (data: {
    name: string;
    nickname: string;
    dob: string;
    mood: string;
    statusMessage: string;
    avatarUrl?: string;
  }) => void;
  onNavigate: (screen: ScreenType) => void;
}

export const EditProfileScreen: React.FC<EditProfileScreenProps> = ({
  partner,
  userNickname,
  onSaveProfile,
  onNavigate,
}) => {
  const [name, setName] = useState(partner.name);
  const [nickname, setNickname] = useState(userNickname || partner.nickname || 'My Love');
  const [dob, setDob] = useState(partner.dob || '1995-08-15');
  const [selectedMood, setSelectedMood] = useState(partner.currentMood || 'Loving');
  const [statusMessage, setStatusMessage] = useState(
    partner.statusMessage || 'Thinking about our next date night.'
  );
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null);

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setAvatarPreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    onSaveProfile({
      name,
      nickname,
      dob,
      mood: selectedMood,
      statusMessage,
      avatarUrl: avatarPreview || undefined,
    });
    onNavigate('profile');
  };

  return (
    <div className="pt-24 pb-32 px-6 max-w-xl mx-auto flex flex-col gap-6 relative z-10">
      {/* Profile Picture Upload */}
      <section className="flex flex-col items-center gap-3">
        <div className="relative group cursor-pointer">
          <div className="w-32 h-32 rounded-full overflow-hidden border-2 border-[#ffcbd5]/50 shadow-[0_0_30px_rgba(244,167,185,0.4)] relative transition-transform duration-300 group-hover:scale-105">
            <img
              src={avatarPreview || partner.avatarUrl}
              alt="Profile Picture"
              className="w-full h-full object-cover"
            />
            <label className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 cursor-pointer">
              <span className="material-symbols-outlined text-white text-3xl">
                photo_camera
              </span>
              <input
                type="file"
                accept="image/*"
                onChange={handlePhotoUpload}
                className="hidden"
              />
            </label>
          </div>
          <div className="absolute bottom-0 right-0 bg-[#f4a7b9] text-[#733949] p-2 rounded-full shadow-lg border border-white/20">
            <span className="material-symbols-outlined text-[16px]">
              edit
            </span>
          </div>
        </div>
        <p className="font-body text-xs text-[#d6c1c5] uppercase tracking-widest font-semibold">
          Update Photo
        </p>
      </section>

      {/* Form Fields */}
      <form onSubmit={handleSave} className="flex flex-col gap-4">
        {/* Username */}
        <div className="glass-card rounded-xl p-5 flex flex-col gap-1.5 shadow-[0_0_20px_rgba(244,167,185,0.2)] bg-white/10 border border-white/20">
          <label className="font-body text-xs text-[#d6c1c5] font-semibold uppercase tracking-wider">
            Username
          </label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Your username"
            required
            className="bg-transparent border-b border-white/20 text-[#dae2fd] font-body text-base py-1.5 glow-input focus:border-b-[#f4a7b9] transition-all"
          />
        </div>

        {/* Partner Nickname */}
        <div className="glass-card rounded-xl p-5 flex flex-col gap-1.5 shadow-[0_0_20px_rgba(244,167,185,0.2)] bg-white/10 border border-white/20">
          <label className="font-body text-xs text-[#d6c1c5] font-semibold uppercase tracking-wider">
            Partner Nickname
          </label>
          <input
            type="text"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            placeholder="What you call them"
            required
            className="bg-transparent border-b border-white/20 text-[#dae2fd] font-body text-base py-1.5 glow-input focus:border-b-[#f4a7b9] transition-all"
          />
        </div>

        {/* Birthday */}
        <div className="glass-card rounded-xl p-5 flex flex-col gap-1.5 shadow-[0_0_20px_rgba(244,167,185,0.2)] bg-white/10 border border-white/20">
          <label className="font-body text-xs text-[#d6c1c5] font-semibold uppercase tracking-wider">
            Birthday
          </label>
          <input
            type="date"
            value={dob}
            onChange={(e) => setDob(e.target.value)}
            required
            className="bg-transparent border-b border-white/20 text-[#dae2fd] font-body text-base py-1.5 glow-input focus:border-b-[#f4a7b9] transition-all"
          />
        </div>

        {/* Current Mood Chips */}
        <div className="glass-card rounded-xl p-5 flex flex-col gap-3 shadow-[0_0_20px_rgba(244,167,185,0.2)] bg-white/10 border border-white/20">
          <label className="font-body text-xs text-[#d6c1c5] font-semibold uppercase tracking-wider">
            Current Mood
          </label>
          <div className="flex gap-3">
            {[
              { label: 'Loving', icon: 'favorite' },
              { label: 'Sleepy', icon: 'bedtime' },
              { label: 'Busy', icon: 'work' },
            ].map((m) => {
              const isSel = selectedMood.toLowerCase() === m.label.toLowerCase();
              return (
                <button
                  key={m.label}
                  type="button"
                  onClick={() => setSelectedMood(m.label)}
                  className={`flex-1 py-2.5 rounded-full border flex items-center justify-center gap-2 transition-all cursor-pointer ${
                    isSel
                      ? 'border-[#ffcbd5] text-[#ffcbd5] bg-[#ffcbd5]/20 shadow-[0_0_15px_rgba(244,167,185,0.3)] font-semibold'
                      : 'border-white/15 text-[#d6c1c5] hover:bg-white/5'
                  }`}
                >
                  <span
                    className="material-symbols-outlined text-[18px]"
                    data-weight={isSel ? 'fill' : 'none'}
                    style={{ fontVariationSettings: isSel ? "'FILL' 1" : "'FILL' 0" }}
                  >
                    {m.icon}
                  </span>
                  <span className="font-body text-sm">{m.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Status Message */}
        <div className="glass-card rounded-xl p-5 flex flex-col gap-2 shadow-[0_0_20px_rgba(244,167,185,0.2)] bg-white/10 border border-white/20">
          <label className="font-body text-xs text-[#d6c1c5] font-semibold uppercase tracking-wider">
            Status Message
          </label>
          <textarea
            value={statusMessage}
            onChange={(e) => setStatusMessage(e.target.value)}
            rows={3}
            placeholder="What's on your mind?"
            className="bg-transparent border border-white/15 rounded-xl text-[#dae2fd] font-body text-sm p-3 glow-input focus:border-[#f4a7b9] transition-all resize-none"
          />
        </div>

        {/* Save Button */}
        <button
          type="submit"
          className="mt-2 w-full bg-[#ffcbd5] text-[#521f2e] font-display font-bold text-base py-4 rounded-full shadow-[0_0_20px_rgba(244,167,185,0.4)] hover:shadow-[0_0_30px_rgba(244,167,185,0.6)] hover:bg-[#ffd9e0] transition-all active:scale-98 cursor-pointer"
        >
          Save Changes
        </button>
      </form>
    </div>
  );
};
