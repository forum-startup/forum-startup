import { ref } from 'vue'
import api from '../utils/axios.js'
import { currentUser } from '../utils/store.js'

export function usePosts() {
    const posts = ref([])
    const isLoading = ref(false)
    const errors = ref(null)

    async function fetchCurrentUserPosts() {
        if (!currentUser.value?.id) {
            errors.value = 'You must be logged in'
            return
        }

        isLoading.value = true
        errors.value = null

        try {
            const res = await api.get(`/public/posts/by-author/${currentUser.value.id}`)
            posts.value = res.data
        } catch (err) {
            errors.value = err.response?.data?.message || 'Failed to load your posts'
        } finally {
            isLoading.value = false
        }
    }

    async function fetchPostsByUserId(id) {
        if (!id) return

        isLoading.value = true
        errors.value = null

        try {
            const res = await api.get(`/public/posts/by-author/${id}`)
            posts.value = res.data
        } catch (err) {
            errors.value = err.response?.data?.message || 'Failed to load posts'
        } finally {
            isLoading.value = false
        }
    }

    return {
        posts,
        isLoading,
        errors,
        fetchCurrentUserPosts,
        fetchPostsByUserId,
    }
}