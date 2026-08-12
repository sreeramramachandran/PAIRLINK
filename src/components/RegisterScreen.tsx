import React, { useState } from 'react';
import { ScreenType } from '../types';

interface RegisterScreenProps {
  onNavigate: (screen: ScreenType) => void;
  onRegisterSuccess: (username: string, phone: string, dob: string) => void;
}

export const RegisterScreen: React.FC<RegisterScreenProps> = ({
  onNavigate,
  onRegisterSuccess,
}) => {
  const [username, setUsername] = useState('Alex');
  const [phone, setPhone] = useState('9876543210');
  const [dob, setDob] = useState('1995-08-15');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [photoPreview, setPhotoPreview] = useState<string | null>(null);

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPhotoPreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (password && password !== confirmPassword) {
      alert('Passwords do not match!');
      return;
    }
    onRegisterSuccess(username || 'Alex', phone, dob);
    onNavigate('register_success');
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6 relative z-10 py-12">
      <main className="w-full max-w-[420px] glass-panel rounded-[2.5rem] p-8 md:p-10 flex flex-col relative z-10 bg-white/10 border border-white/20 shadow-[0_25px_50px_-12px_rgba(0,0,0,0.5)]">
        {/* Header */}
        <header className="text-center mb-8">
          <h1 className="font-display font-bold text-3xl md:text-4xl text-[#ffcbd5] tracking-tight mb-2">
            PairLink
          </h1>
          <p className="font-body text-[#d6c1c5] text-sm md:text-base">
            Create your digital sanctuary.
          </p>
        </header>

        {/* Form Elements */}
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          {/* Profile Picture Upload */}
          <div className="flex flex-col items-center mb-2">
            <div className="relative group cursor-pointer">
              <div className="w-24 h-24 rounded-full glass-panel flex items-center justify-center overflow-hidden border border-white/20 group-hover:border-[#f4a7b9] transition-colors duration-300 relative z-10 bg-white/15">
                {photoPreview ? (
                  <img
                    src={photoPreview}
                    alt="Profile preview"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <span className="material-symbols-outlined text-4xl text-[#d6c1c5] group-hover:text-[#ffcbd5] transition-colors">
                    add_a_photo
                  </span>
                )}
                <input
                  type="file"
                  accept="image/*"
                  onChange={handlePhotoUpload}
                  aria-label="Upload profile picture"
                  className="absolute inset-0 opacity-0 cursor-pointer z-20"
                />
              </div>
              <div className="absolute inset-0 bg-[#f4a7b9]/20 rounded-full blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none" />
            </div>
            <span className="text-[12px] text-[#d6c1c5]/80 mt-2 font-body">
              Upload photo (optional)
            </span>
          </div>

          {/* Username Input */}
          <div className="relative">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 pointer-events-none text-[20px]">
              person
            </span>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Username"
              required
              className="glass-input w-full rounded-2xl py-3.5 pl-12 pr-4 font-body text-[#dae2fd] bg-white/10"
            />
          </div>

          {/* Phone Number Input */}
          <div className="relative">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 pointer-events-none text-[20px]">
              call
            </span>
            <input
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="Phone Number"
              required
              className="glass-input w-full rounded-2xl py-3.5 pl-12 pr-4 font-body text-[#dae2fd] bg-white/10"
            />
          </div>

          {/* Date of Birth Input */}
          <div className="relative">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 pointer-events-none text-[20px]">
              calendar_month
            </span>
            <input
              type="date"
              value={dob}
              onChange={(e) => setDob(e.target.value)}
              required
              className="glass-input w-full rounded-2xl py-3.5 pl-12 pr-4 font-body text-[#dae2fd] bg-white/10"
            />
          </div>

          {/* Password Input */}
          <div className="relative group">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 pointer-events-none transition-colors group-focus-within:text-[#ffcbd5] text-[20px]">
              lock
            </span>
            <input
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
              required
              className="glass-input w-full rounded-2xl py-3.5 pl-12 pr-12 font-body text-[#dae2fd] bg-white/10"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 hover:text-[#ffcbd5] transition-colors focus:outline-none p-1"
            >
              <span className="material-symbols-outlined text-[20px]">
                {showPassword ? 'visibility_off' : 'visibility'}
              </span>
            </button>
          </div>

          {/* Confirm Password Input */}
          <div className="relative group">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 pointer-events-none transition-colors group-focus-within:text-[#ffcbd5] text-[20px]">
              lock_reset
            </span>
            <input
              type={showConfirmPassword ? 'text' : 'password'}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm Password"
              required
              className="glass-input w-full rounded-2xl py-3.5 pl-12 pr-12 font-body text-[#dae2fd] bg-white/10"
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-[#d6c1c5]/70 hover:text-[#ffcbd5] transition-colors focus:outline-none p-1"
            >
              <span className="material-symbols-outlined text-[20px]">
                {showConfirmPassword ? 'visibility_off' : 'visibility'}
              </span>
            </button>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="mt-4 bg-[#f4a7b9]/40 text-[#ffcbd5] w-full rounded-full py-4 font-body text-xs uppercase tracking-[0.1em] font-bold glow-button relative overflow-hidden group border border-white/20 hover:bg-[#f4a7b9]/60 active:scale-98 transition-all"
          >
            <span className="relative z-10 flex items-center justify-center gap-2">
              Register
              <span className="material-symbols-outlined text-[18px] opacity-0 -translate-x-2 group-hover:opacity-100 group-hover:translate-x-0 transition-all duration-300">
                arrow_forward
              </span>
            </span>
          </button>
        </form>

        {/* Footer Links */}
        <div className="text-center mt-6">
          <button
            onClick={() => onNavigate('login')}
            className="font-body text-sm text-[#d6c1c5] hover:text-[#ffcbd5] transition-colors inline-flex items-center justify-center gap-1 group"
          >
            Already have an account?{' '}
            <span className="text-[#ffcbd5] font-semibold border-b border-transparent group-hover:border-[#ffcbd5] transition-colors ml-1">
              Log in
            </span>
          </button>
        </div>
      </main>
    </div>
  );
};
