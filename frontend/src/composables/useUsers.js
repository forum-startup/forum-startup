import {ref} from "vue";
import api from "../utils/axios.js";
import router from "../router/router.js";
import {toast} from "vue3-toastify";

export function useUsers() {
    const count = ref(null)
    const users = ref([])
    const isLoading = ref(false)
    const isBlocking = ref(new Set()) // tracks which users are being blocked/unblocked
    const error = ref(null)

    async function fetchTotalUserCount() {
        isLoading.value = true
        error.value = null

        try {
            const response = await api.get("/public/users/count")
            count.value = response.data
        } catch (e) {
            error.value = err.response?.data?.message || 'Failed to load user count'
        } finally {
            isLoading.value = false
        }
    }

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

            toast.success("User blocked successfully!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to block user'
            throw err
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

            toast.success("User unblocked successfully!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

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

            toast.success("User promoted successfully!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

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

            toast.success("Account deleted successfully!", {
                autoClose: 3000,
                position: toast.POSITION.TOP_RIGHT,
                theme: "dark",
            })

        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to delete account'
        }
    }

    return {
        count,
        users,
        isLoading,
        isBlocking: (id) => isBlocking.value.has(id), // helper for button state
        error,
        fetchTotalUserCount,
        fetchUsers,
        fetchUserById,
        blockUser,
        unblockUser,
        promoteToAdmin,
        deleteAccount,
    }

}