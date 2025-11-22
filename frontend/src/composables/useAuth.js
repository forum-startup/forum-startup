import { useRouter } from 'vue-router'
import api from '../utils/axios.js'
import { logout } from '../utils/auth'

export function useAuth() {
    const router = useRouter()

    const deleteAccount = async () => {
        if (!confirm('Delete your account permanently? This cannot be undone.')) return

        try {
            await api.delete('/private/users/me')
            await logout()
            await router.push('/')
        } catch (err) {
            alert('Failed to delete account')
        }
    }

    return { deleteAccount }
}