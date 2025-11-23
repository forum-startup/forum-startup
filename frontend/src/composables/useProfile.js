import { ref } from 'vue'
import api from '../utils/axios.js'
import { currentUser } from '../utils/store'

export function useProfile() {
    const loading = ref(false)
    const error = ref('')

    const profile = ref({
        firstName: '',
        lastName: '',
        email: '',
        username: '',
        profilePhotoUrl: null,
        createdAt: ''
    })

    const avatarPreview = ref('')

    // Fallback avatar using username initial
    const getFallbackAvatar = (username) => {
        if (!username) return 'https://ui-avatars.com/api/?name=U&background=6366f1&color=fff&bold=true'
        const initial = username[0].toUpperCase()
        return `https://ui-avatars.com/api/?name=${initial}&background=6366f1&color=fff&bold=true`
    }

    const loadProfile = async () => {
        try {
            loading.value = true
            const { data } = await api.get('/private/users/profile')

            profile.value = data
            avatarPreview.value = data.profilePhotoUrl || getFallbackAvatar(data.username)

            // Keep your global store in sync
            if (currentUser.value) {
                Object.assign(currentUser.value, data)
            }
        } catch (err) {
            error.value = err.response?.data || 'Failed to load profile'
            console.error(err)
        } finally {
            loading.value = false
        }
    }

    const updateProfile = async (updates) => {
        try {
            loading.value = true
            error.value = ''

            const payload = {
                firstName: updates.firstName ?? profile.value.firstName,
                lastName: updates.lastName ?? profile.value.lastName,
                email: updates.email ?? profile.value.email,
                profilePhotoUrl: updates.profilePhotoUrl ?? profile.value.profilePhotoUrl,
                password: updates.password || undefined
            }

            const { data } = await api.put('/private/users/me', payload)

            profile.value = data
            avatarPreview.value = data.profilePhotoUrl || avatarPreview.value

            alert('Profile updated successfully!')
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to update profile'
            throw err
        } finally {
            loading.value = false
        }
    }

    const handleAvatarChange = (e) => {
        const file = e.target.files[0]
        if (!file) return

        if (file.size > 2 * 1024 * 1024) {
            alert('Image must be under 2MB')
            return
        }

        const reader = new FileReader()
        reader.onload = (ev) => {
            const dataUrl = ev.target.result
            avatarPreview.value = dataUrl
            // Save immediately or wait for "Save" button
            updateProfile({ profilePhotoUrl: dataUrl })
        }
        reader.readAsDataURL(file)
    }

    return {
        profile: profile,
        avatarPreview,
        loading,
        error,
        loadProfile,
        updateProfile,
        handleAvatarChange
    }
}