export interface UserSelfUpdateDto {
    firstName: string
    lastName: string
    email: string
    profilePhotoUrl?: string | null
    password?: string | null
}

export interface ProfileResponseDto {
    firstName: string
    lastName: string
    email: string
    username: string
    profilePhotoUrl: string | null
    createdAt: string
}