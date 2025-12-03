import {ref} from "vue";
import api from "../utils/axios.js";
import router from "../router/router.js";

export function useUsers() {
    const users = ref([])
    const isLoading = ref(false)
    const isBlocking = ref(new Set()) // tracks which users are being blocked/unblocked
    const error = ref(null)

    async function fetchUsers() {
        isLoading.value = true
        error.value = null

        try {
            const res = await api.get("/admin/users")
            users.value = res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to load users'
        } finally {
            isLoading.value = false
        }
    }

    async function fetchUserById(id) {
        error.value = null

        try {
            const res = await api.get(`/admin/users/${id}`)
            users.value = res.data
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to load user'
        }
    }

    async function blockUser(id) {
        error.value = null
        isBlocking.value.add(id)

        try {
            await api.put(`/admin/users/${id}/block`)
            const user = users.value.find(u => u.id === id)
            if (user) user.isBlocked = true
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to block user'
            throw err // re-throw so UI can react
        } finally {
            isBlocking.value.delete(id)
        }
    }

    async function unblockUser(id) {
        error.value = null
        isBlocking.value.add(id)

        try {
            await api.put(`/admin/users/${id}/unblock`)
            const user = users.value.find(u => u.id === id)
            if (user) user.isBlocked = false
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to unblock user'
            throw err
        } finally {
            isBlocking.value.delete(id)
        }
    }

    async function promoteToAdmin(id) {
        error.value = null

        try {
            await api.put(`/admin/users/${id}/promote`)
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to promote user'
            throw err
        }
    }

    const deleteAccount = async () => {
        error.value = null

        if (!confirm('Delete your account permanently? This cannot be undone.')) return

        try {
            await api.delete('/private/users/me')
            await logout()
            await router.push('/')
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete account'
        }
    }

    return {
        users,
        isLoading,
        isBlocking: (id) => isBlocking.value.has(id), // helper for button state
        error,
        fetchUsers,
        fetchUserById,
        blockUser,
        unblockUser,
        promoteToAdmin,
        deleteAccount
    }

}