import {ref, computed} from 'vue'
import {useComment} from './useComment'
import {currentUser} from '../utils/store.js'
import {useAuth} from "./useAuth.js";

export function useCommentLogic(comment, allComments, postId) {
    const {form, isLoading, errors, editComment, deleteComment, adminDeleteComment} = useComment()
    const {hasRole} = useAuth()

    const isOwnComment = computed(() => currentUser.value?.id === comment.value?.creatorId)
    const isAdmin = computed(() => hasRole('ROLE_ADMIN'))
    const canEdit = computed(() => isOwnComment.value && !comment.value?.deleted)
    const canDelete = computed(() => (isOwnComment.value || isAdmin.value) && !comment.value?.deleted)

    const isEditing = ref(false)

    function startEdit() {
        isEditing.value = true
        form.value.postId = postId
        form.value.content = comment.value.content
    }

    function cancelEdit() {
        isEditing.value = false
        form.value.content = ''
    }

    async function submitEdit() {
        const updated = await editComment(comment.value.id)
        if (updated) {
            isEditing.value = false
            return updated
        }
    }

    async function handleDelete() {
        if (!confirm('Delete?')) return false
        return isAdmin.value
            ? await adminDeleteComment(comment.value.id)
            : await deleteComment(comment.value.id)
    }

    return {
        isEditing,
        canEdit,
        canDelete,
        startEdit,
        cancelEdit,
        submitEdit,
        handleDelete,
        isLoading,
        errors,
    }
}