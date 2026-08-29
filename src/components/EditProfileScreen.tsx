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
    <div className="pt-20 pb-32 px-4 sm:px-6 max-w-lg mx-auto flex flex-col gap-5 relative z-10">
      {/* Profile Picture Upload */}
      <section className="flex flex-col items-center gap-2 pt-2">
        <div className="relative group cursor-pointer">
          <div className="w-28 h-28 rounded-full overflow-hidden border-4 border-white shadow-xl p-0.5 bg-white relative transition-transform duration-300 group-hover:scale-105">
            <img
              src={avatarPreview || partner.avatarUrl}
              alt="Profile Picture"
              className="w-full h-full rounded-full object-cover"
            />
            <label className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 cursor-pointer">
              <span className="material-symbols-outlined text-white text-2xl">
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
          <div className="absolute bottom-0 right-0 bg-[#E60039] text-white p-2 rounded-full shadow-md border-2 border-white">
            <span className="material-symbols-outlined text-[14px]">
              edit
            </span>
          </div>
        </div>
        <p className="font-body text-[11px] text-[#7A6E73] uppercase tracking-widest font-bold">
          Update Photo
        </p>
      </section>

      {/* Form Fields */}
      <form onSubmit={handleSave} className="flex flex-col gap-4">
        {/* Username */}
        <div className="bg-white/90 backdrop-blur-2xl rounded-3xl p-5 flex flex-col gap-1.5 border border-white/90 shadow-[0_8px_24px_rgba(230,0,57,0.05)]">
          <label className="font-body text-[10px] text-[#7A6E73] font-bold uppercase tracking-wider">
            Username
          </label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Your username"
            required
            className="w-full bg-white border border-rose-200/80 px-4 py-3 text-sm text-[#2D2226] font-display font-bold rounded-2xl focus:border-[#E60039] outline-none"
          />
        </div>

        {/* Partner Nickname */}
        <div className="bg-white/90 backdrop-blur-2xl rounded-3xl p-5 flex flex-col gap-1.5 border border-white/90 shadow-[0_8px_24px_rgba(230,0,57,0.05)]">
          <label className="font-body text-[10px] text-[#7A6E73] font-bold uppercase tracking-wider">
            Partner Nickname
          </label>
          <input
            type="text"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            placeholder="What you call them"
            required
            className="w-full bg-white border border-rose-200/80 px-4 py-3 text-sm text-[#2D2226] font-display font-bold rounded-2xl focus:border-[#E60039] outline-none"
          />
        </div>

        {/* Birthday */}
        <div className="bg-white/90 backdrop-blur-2xl rounded-3xl p-5 flex flex-col gap-1.5 border border-white/90 shadow-[0_8px_24px_rgba(230,0,57,0.05)]">
          <label className="font-body text-[10px] text-[#7A6E73] font-bold uppercase tracking-wider">
            Birthday
          </label>
          <input
            type="date"
            value={dob}
            onChange={(e) => setDob(e.target.value)}
            required
            className="w-full bg-white border border-rose-200/80 px-4 py-3 text-sm text-[#2D2226] font-display font-bold rounded-2xl focus:border-[#E60039] outline-none"
          />
        </div>

        {/* Current Mood Chips */}
        <div className="bg-white/90 backdrop-blur-2xl rounded-3xl p-5 flex flex-col gap-2.5 border border-white/90 shadow-[0_8px_24px_rgba(230,0,57,0.05)]">
          <label className="font-body text-[10px] text-[#7A6E73] font-bold uppercase tracking-wider">
            Current Mood
          </label>
          <div className="flex gap-2.5">
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
                  className={`flex-1 py-2.5 rounded-2xl border flex items-center justify-center gap-1.5 transition-all cursor-pointer ${
                    isSel
                      ? 'border-[#E60039] text-[#E60039] bg-rose-50 font-bold shadow-xs'
                      : 'border-rose-200/80 text-[#7A6E73] hover:bg-rose-50/50'
                  }`}
                >
                  <span
                    className="material-symbols-outlined text-[16px]"
                    style={{ fontVariationSettings: isSel ? "'FILL' 1" : "'FILL' 0" }}
                  >
                    {m.icon}
                  </span>
                  <span className="font-body text-xs">{m.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Status Message */}
        <div className="bg-white/90 backdrop-blur-2xl rounded-3xl p-5 flex flex-col gap-1.5 border border-white/90 shadow-[0_8px_24px_rgba(230,0,57,0.05)]">
          <label className="font-body text-[10px] text-[#7A6E73] font-bold uppercase tracking-wider">
            Status Message
          </label>
          <textarea
            value={statusMessage}
            onChange={(e) => setStatusMessage(e.target.value)}
            rows={3}
            placeholder="What's on your mind?"
            className="w-full bg-white border border-rose-200/80 rounded-2xl text-[#2D2226] font-body text-xs p-3 focus:border-[#E60039] outline-none resize-none"
          />
        </div>

        {/* Save Button */}
        <button
          type="submit"
          className="glow-button text-white font-display font-bold text-base py-4 rounded-full flex items-center justify-center gap-2 active:scale-98 transition-all cursor-pointer mt-1"
        >
          Save Changes
        </button>
      </form>
    </div>
  );
};
