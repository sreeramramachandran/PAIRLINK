import React, { useState } from 'react';
import { ScreenType } from '../types';
import { PairLinkLogo } from './PairLinkLogo';

interface LoginScreenProps {
  onNavigate: (screen: ScreenType) => void;
  onLoginSuccess: (phone: string) => void;
}

export const LoginScreen: React.FC<LoginScreenProps> = ({
  onNavigate,
  onLoginSuccess,
}) => {
  const [phone, setPhone] = useState('9876543210');
  const [password, setPassword] = useState('••••••••');
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onLoginSuccess(phone);
    onNavigate('home');
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6 relative z-10">
      <main className="w-full max-w-md">
        <div className="bg-white/90 backdrop-blur-2xl rounded-3xl p-8 md:p-10 flex flex-col items-center border border-white/90 shadow-[0_16px_40px_rgba(230,0,57,0.1),0_4px_16px_rgba(0,0,0,0.04)] modal-animate">
          {/* Brand Logo */}
          <div className="mb-6 text-center">
            <PairLinkLogo size="lg" showTagline={true} />
            <p className="font-body text-sm text-[#7A6E73] font-medium mt-3">
              Enter your sanctuary.
            </p>
          </div>

          {/* Login Form */}
          <form onSubmit={handleSubmit} className="w-full flex flex-col gap-4">
            {/* Phone Number Input */}
            <div className="relative w-full">
              <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#7A6E73] pointer-events-none text-[20px]">
                call
              </span>
              <input
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="Phone Number"
                required
                className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] placeholder:text-[#7A6E73]/60 pl-12 pr-4 py-3.5 font-body text-sm rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all"
              />
            </div>

            {/* Password Input */}
            <div className="relative w-full">
              <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-[#7A6E73] pointer-events-none text-[20px]">
                lock
              </span>
              <input
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Password"
                required
                className="w-full bg-white/90 border border-rose-200/80 text-[#2D2226] placeholder:text-[#7A6E73]/60 pl-12 pr-12 py-3.5 font-body text-sm rounded-2xl focus:border-[#E60039] focus:ring-4 focus:ring-[#E60039]/10 outline-none transition-all"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-[#7A6E73] hover:text-[#E60039] transition-colors focus:outline-none p-1"
                aria-label="Toggle password visibility"
              >
                <span className="material-symbols-outlined text-[20px]">
                  {showPassword ? 'visibility_off' : 'visibility'}
                </span>
              </button>
            </div>

            {/* Forgot Password Link */}
            <div className="flex justify-end">
              <button
                type="button"
                onClick={() => alert('Password reset link sent to your registered phone number!')}
                className="font-body text-xs text-[#E60039] hover:underline font-semibold"
              >
                Forgot Password?
              </button>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              className="w-full mt-2 glow-button text-white font-display font-bold text-base py-3.5 rounded-full flex items-center justify-center gap-2 active:scale-98 transition-all cursor-pointer"
            >
              <span>Login</span>
              <span className="material-symbols-outlined text-[20px]">
                arrow_forward
              </span>
            </button>
          </form>

          {/* Register Link */}
          <div className="mt-8 text-center font-body text-sm text-[#7A6E73]">
            Don't have an account?{' '}
            <button
              onClick={() => onNavigate('register')}
              className="text-[#E60039] hover:text-[#C4002F] font-extrabold transition-colors underline underline-offset-4 decoration-[#E60039]/40"
            >
              Register
            </button>
          </div>
        </div>
      </main>
    </div>
  );
};
