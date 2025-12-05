import { ref } from 'vue'
import api from '../utils/axios.js'
import { currentUser } from '../utils/store.js'

export function usePosts() {
    const count = ref(null)
    const posts = ref([])
    const isLoading = ref(false)
    const errors = ref(null)

    async function fetchTotalPostCount() {
        isLoading.value = true
        errors.value = null

        try {
            const response = await api.get("/public/posts/count")
            count.value = response.data
        } catch (e) {
            errors.value = err.response?.data?.message || 'Failed to load post count'
        } finally {
            isLoading.value = false
        }
    }

    async function fetchRecentPosts() {
        isLoading.value = true
        errors.value = null

        try {
            const response = await api.get("/public/posts/recent?limit=10")
            posts.value = response.data
        } catch (e) {
            errors.value = err.response?.data?.message || 'Failed to load recent posts'
        } finally {
            isLoading.value = false
        }
    }

    async function fetchCurrentUserPosts() {
        if (!currentUser.value?.id) {
            errors.value = 'You must be logged in'
            return
        }

        isLoading.value = true
        errors.value = null

        try {
            const res = await api.get(`/private/posts/by-author/${currentUser.value.id}`)
            posts.value = res.data
        } catch (err) {
            errors.value = err.response?.data?.message || 'Failed to load your posts'
        } finally {
            isLoading.value = false
        }
    }

    async function fetchAllPosts() {
        if (!currentUser.value?.id) {
            errors.value = 'You must be logged in'
            return
        }

        isLoading.value = true
        errors.value = null

        try {
            const res = await api.get(`/private/posts`)
            posts.value = res.data.content
        } catch (err) {
            errors.value = err.response?.data?.message || 'Failed to load posts'
        } finally {
            isLoading.value = false
        }
    }

    async function filterPosts(
        page = 0,
        size = 10,
        sort = undefined,
        searchQuery = undefined,
    ) {

        isLoading.value = true
        errors.value = null

        try {
            const params = Object.fromEntries(
                Object.entries({ page, size, sort, searchQuery })
                    .filter(([_, v]) => v !== undefined)
            );

            const res = await api.get(
                "/private/posts",
                {params}
            )
            posts.value = res.data.content
        } catch (err) {
            errors.value = err.response?.data?.message || 'Failed to load posts'
        } finally {
            isLoading.value = false
        }
    }

    async function fetchPostsByUserId(userId) {
        if (!userId) return

        isLoading.value = true
        errors.value = null

        try {
            const res = await api.get(`/private/posts/by-author/${userId}`)
            posts.value = res.data
        } catch (err) {
            errors.value = err.response?.data?.message || 'Failed to load posts'
        } finally {
            isLoading.value = false
        }
    }

    return {
        count,
        posts,
        isLoading,
        errors,
        fetchRecentPosts,
        fetchCurrentUserPosts,
        fetchAllPosts,
        fetchPostsByUserId,
        fetchTotalPostCount,
        filterPosts,
    }
}