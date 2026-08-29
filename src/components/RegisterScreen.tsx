import React, { useState } from 'react';
import { ScreenType } from '../types';
import { PairLinkLogo } from './PairLinkLogo';

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
      <main className="w-full max-w-[420px] bg-white/90 backdrop-blur-2xl rounded-3xl p-8 md:p-10 flex flex-col border border-white/90 shadow-[0_16px_40px_rgba(230,0,57,0.1),0_4px_16px_rgba(0,0,0,0.04)] modal-animate">
        {/* Header */}
        <header className="text-center mb-6">
          <PairLinkLogo size="md" showTagline={true} />
          <p className="font-body text-[#7A6E73] text-sm font-medium mt-3">
            Create your digital sanctuary.
          </p>
        </header>

        {/* Form Elements */}
        <form onSubmit={handleSubmit} className="flex flex-col gap-3.5">
          {/* Profile Picture Upload */}
          <div className="flex flex-col items-center mb-1">
            <div className="relative group cursor-pointer">
              <div className="w-20 h-20 rounded-full bg-rose-50 border-2 border-rose-200 flex items-center justify-center overflow-hidden group-hover:border-[#E60039] transition-colors relative z-10 shadow-sm">
                {photoPreview ? (
                  <img
                    src={photoPreview}
                    alt="Profile preview"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <span className="material-symbols-outlined text-3xl text-[#E60039]">
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
            </div>
            <span className="text-[11px] text-[#7A6E73] mt-1.5 font-medium">
              Upload photo (optional)
            </span>
          </div>

          {/* Username Input */}
          <div className="relative">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#7A6E73] pointer-events-none text-[20px]">
              person
            </span>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Username"
              required
              className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] placeholder:text-[#7A6E73]/60 pl-12 pr-4 py-3 font-body text-sm rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all"
            />
          </div>

          {/* Phone Number Input */}
          <div className="relative">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#7A6E73] pointer-events-none text-[20px]">
              call
            </span>
            <input
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="Phone Number"
              required
              className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] placeholder:text-[#7A6E73]/60 pl-12 pr-4 py-3 font-body text-sm rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all"
            />
          </div>

          {/* Date of Birth Input */}
          <div className="relative">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#7A6E73] pointer-events-none text-[20px]">
              calendar_month
            </span>
            <input
              type="date"
              value={dob}
              onChange={(e) => setDob(e.target.value)}
              required
              className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] placeholder:text-[#7A6E73]/60 pl-12 pr-4 py-3 font-body text-sm rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all"
            />
          </div>

          {/* Password Input */}
          <div className="relative">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#7A6E73] pointer-events-none text-[20px]">
              lock
            </span>
            <input
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
              required
              className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] placeholder:text-[#7A6E73]/60 pl-12 pr-12 py-3 font-body text-sm rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-[#7A6E73] hover:text-[#E60039] transition-colors focus:outline-none p-1"
            >
              <span className="material-symbols-outlined text-[20px]">
                {showPassword ? 'visibility_off' : 'visibility'}
              </span>
            </button>
          </div>

          {/* Confirm Password Input */}
          <div className="relative">
            <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#7A6E73] pointer-events-none text-[20px]">
              lock_reset
            </span>
            <input
              type={showConfirmPassword ? 'text' : 'password'}
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm Password"
              required
              className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] placeholder:text-[#7A6E73]/60 pl-12 pr-12 py-3 font-body text-sm rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all"
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-[#7A6E73] hover:text-[#E60039] transition-colors focus:outline-none p-1"
            >
              <span className="material-symbols-outlined text-[20px]">
                {showConfirmPassword ? 'visibility_off' : 'visibility'}
              </span>
            </button>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="mt-3 glow-button text-white w-full rounded-full py-3.5 font-display text-sm font-bold flex items-center justify-center gap-2 active:scale-98 transition-all cursor-pointer"
          >
            <span>Register</span>
            <span className="material-symbols-outlined text-[18px]">
              arrow_forward
            </span>
          </button>
        </form>

        {/* Footer Links */}
        <div className="text-center mt-6">
          <button
            onClick={() => onNavigate('login')}
            className="font-body text-sm text-[#7A6E73] hover:text-[#E60039] transition-colors"
          >
            Already have an account?{' '}
            <span className="text-[#E60039] font-bold underline underline-offset-4 decoration-[#E60039]/40 ml-1">
              Log in
            </span>
          </button>
        </div>
      </main>
    </div>
  );
};
