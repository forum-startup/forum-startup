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
  <div class="min-h-screen bg-gray-900 py-10">
    <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
      <div class="divide-y divide-white/5">
        <div class="grid grid-cols-1 gap-x-8 gap-y-10 py-16 md:grid-cols-3">
          <div>
            <h2 class="text-lg font-semibold text-white">Personal Information</h2>
            <p class="mt-1 text-sm text-gray-400">Update your profile details.</p>
          </div>

          <form @submit.prevent="saveProfile" class="md:col-span-2 space-y-8">
            <!-- Avatar -->
            <div class="flex items-center gap-8">
              <img
                  :src="avatarPreview"
                  class="h-24 w-24 rounded-full ring-4 ring-white/10 object-cover"
                  alt="Profile photo"
              >
              <div>
                <label class="cursor-pointer">
                  <span class="rounded-md bg-white/10 px-4 py-2 text-sm font-medium hover:bg-white/20">
                    Change Photo
                  </span>
                  <input type="file" accept="image/*" @change="handleAvatarChange" class="sr-only">
                </label>
                <p class="mt-2 text-xs text-gray-400">JPG, PNG, GIF. Max 2MB.</p>
              </div>
            </div>

            <div class="grid grid-cols-6 gap-6">
              <div class="col-span-3">
                <label class="block text-sm font-medium text-white">First name</label>
                <input v-model="profile.firstName" class="mt-2 block w-full rounded-md bg-white/5 py-2 px-3 text-white ring-1 ring-white/10 focus:ring-2 focus:ring-indigo-500" />
              </div>

              <div class="col-span-3">
                <label class="block text-sm font-medium text-white">Last name</label>
                <input v-model="profile.lastName" class="mt-2 block w-full rounded-md bg-white/5 py-2 px-3 text-white ring-1 ring-white/10 focus:ring-2 focus:ring-indigo-500" />
              </div>

              <div class="col-span-6">
                <label class="block text-sm font-medium text-white">Email</label>
                <input v-model="profile.email" type="email" class="mt-2 block w-full rounded-md bg-white/5 py-2 px-3 text-white ring-1 ring-white/10 focus:ring-2 focus:ring-indigo-500" />
              </div>

              <div class="col-span-6">
                <label class="block text-sm font-medium text-white">Username (cannot be changed)</label>
                <div class="mt-2 px-3 flex rounded-md bg-white/5 ring-1 ring-white/10">
                  <input :value="profile.username" disabled class="flex-1 bg-transparent py-2 text-gray-300" />
                </div>
              </div>

              <div class="col-span-3">
                <label class="block text-sm font-medium text-white">
                  New Password
                </label>
                <input
                    v-model="newPassword"
                    type="password"
                    placeholder="Leave empty to keep current password"
                    class="mt-2 block w-full rounded-md bg-white/5 py-2 px-3 text-white ring-1 ring-white/10 focus:ring-2 focus:ring-indigo-500"
                />
              </div>
            </div>

            <div class="flex gap-4">
              <button
                  type="submit"
                  :disabled="loading"
                  class="rounded-md bg-indigo-600 px-5 py-2.5 font-semibold text-white hover:bg-indigo-500 disabled:opacity-50"
              >
                {{ loading ? 'Saving...' : 'Save Changes' }}
              </button>
              <button
                  type="button"
                  @click="deleteAccount"
                  class="rounded-md bg-red-600 px-5 py-2.5 font-semibold text-white hover:bg-red-500"
              >
                Delete Account
              </button>
            </div>

            <p v-if="error" class="text-red-400 text-sm">{{ error }}</p>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>