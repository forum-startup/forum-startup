<script setup>
import {onMounted, ref} from 'vue'
import { useProfile } from '../composables/useProfile'
import { useAuth } from '../composables/useAuth'

const { profile, avatarPreview, loading, error, loadProfile, updateProfile, handleAvatarChange } = useProfile()
const { deleteAccount } = useAuth()

const newPassword = ref('')

onMounted(() => {
  loadProfile()
})

async function saveProfile() {
  const payload = {
    firstName: profile.value.firstName,
    lastName: profile.value.lastName,
    email: profile.value.email,
    profilePhotoUrl: avatarPreview.value.startsWith('data:') ? avatarPreview.value : undefined,

    ...(newPassword.value && { password: newPassword.value }),
  }

  await updateProfile(payload)
  await loadProfile()

  newPassword.value = ''
}
</script>

<template>
  <!-- This uses the same flex-1 centering as Login/Register -->
  <div class="flex-1 flex items-center justify-center px-6 py-12">
    <div class="w-full max-w-2xl space-y-10">

      <!-- Header -->
      <div class="text-center">
        <h2 class="text-3xl font-bold tracking-tight text-white">
          Profile Settings
        </h2>
        <p class="mt-2 text-lg text-gray-400">
          Update your personal information and preferences
        </p>
      </div>

      <!-- Card -->
      <div class="rounded-2xl bg-gray-800/50 backdrop-blur-sm ring-1 ring-white/10 p-8 shadow-2xl">
        <form @submit.prevent="saveProfile" class="space-y-10">

          <!-- Avatar Section -->
          <div class="flex flex-col items-center sm:flex-row gap-8">
            <img
                :src="avatarPreview"
                alt="Profile photo"
                class="h-32 w-32 rounded-full object-cover ring-4 ring-white/20 shadow-xl"
            >
            <div class="text-center sm:text-left">
              <label class="cursor-pointer">
                <span class="inline-block rounded-lg bg-white/10 px-5 py-3 text-sm font-medium hover:bg-white/20 transition">
                  Change Photo
                </span>
                <input type="file" accept="image/*" @change="handleAvatarChange" class="sr-only">
              </label>
              <p class="mt-3 text-xs text-gray-400">JPG, PNG, GIF up to 2MB</p>
            </div>
          </div>

          <!-- Form Fields -->
          <div class="grid grid-cols-1 gap-8 sm:grid-cols-2">
            <!-- First Name -->
            <div>
              <label class="block text-sm font-medium text-gray-300">First Name</label>
              <input
                  v-model="profile.firstName"
                  required
                  class="mt-2 block w-full rounded-lg border-0 bg-white/10 py-3 px-4 text-white
                       placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500 transition"
                  placeholder="John"
              />
            </div>

            <!-- Last Name -->
            <div>
              <label class="block text-sm font-medium text-gray-300">Last Name</label>
              <input
                  v-model="profile.lastName"
                  required
                  class="mt-2 block w-full rounded-lg border-0 bg-white/10 py-3 px-4 text-white
                       placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500 transition"
                  placeholder="Doe"
              />
            </div>

            <!-- Email -->
            <div class="sm:col-span-2">
              <label class="block text-sm font-medium text-gray-300">Email Address</label>
              <input
                  v-model="profile.email"
                  type="email"
                  required
                  class="mt-2 block w-full rounded-lg border-0 bg-white/10 py-3 px-4 text-white
                       placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500 transition"
                  placeholder="you@example.com"
              />
            </div>

            <!-- Username (disabled) -->
            <div class="sm:col-span-2">
              <label class="block text-sm font-medium text-gray-300">Username</label>
              <div class="mt-2 flex rounded-lg bg-white/5 ring-1 ring-white/10">
                <input
                    :value="profile.username"
                    disabled
                    class="w-full bg-transparent py-3 px-4 text-gray-400 cursor-not-allowed"
                />
                <span class="flex items-center px-4 text-xs text-gray-500">Fixed</span>
              </div>
            </div>

            <!-- New Password -->
            <div class="sm:col-span-2">
              <label class="block text-sm font-medium text-gray-300">
                New Password <span class="text-gray-500 font-normal">(optional)</span>
              </label>
              <input
                  v-model="newPassword"
                  type="password"
                  placeholder="Leave empty to keep current password"
                  class="mt-2 block w-full rounded-lg border-0 bg-white/10 py-3 px-4 text-white
                       placeholder:text-gray-500 focus:ring-2 focus:ring-indigo-500 transition"
              />
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex flex-col sm:flex-row gap-4 pt-6 border-t border-white/10">
            <button
                type="submit"
                :disabled="loading"
                class="flex-1 rounded-lg bg-indigo-600 py-3.5 font-semibold text-white
                     hover:bg-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500
                     disabled:opacity-50 transition"
            >
              {{ loading ? 'Saving Changes...' : 'Save Changes' }}
            </button>

            <button
                type="button"
                @click="deleteAccount"
                class="flex-1 rounded-lg bg-red-600/80 py-3.5 font-semibold text-white
                     hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-500
                     transition backdrop-blur-sm"
            >
              Delete Account
            </button>
          </div>

          <!-- Error Message -->
          <p v-if="error" class="text-center text-sm text-red-400 font-medium">
            {{ error }}
          </p>
        </form>
      </div>

    </div>
  </div>
</template>