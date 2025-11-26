import { ref } from 'vue'

export function usePostValidation(form) {
    const errors = ref({})

    const rules = {
        title: (v) => {
            const len = v?.trim().length || 0
            if (!v?.trim()) return 'Title is required'
            if (len < 16) return 'Title must be at least 16 characters'
            if (len > 64) return 'Title cannot exceed 64 characters'
            return true
        },
        content: (v) => {
            const len = v?.trim().length || 0
            if (!v?.trim()) return 'Content is required'
            if (len < 32) return 'Content must be at least 32 characters'
            if (len > 8192) return 'Content cannot exceed 8192 characters'
            return true
        },
    }

    function validate() {
        errors.value = {}
        let isValid = true

        Object.keys(rules).forEach((key) => {
            const value = form.value[key]
            const result = rules[key](value)
            if (result !== true) {
                errors.value[key] = result
                isValid = false
            }
        })

        return isValid
    }

    function clearErrors() {
        errors.value = {}
    }

    return {
        errors,
        validate,
        clearErrors,
    }
}