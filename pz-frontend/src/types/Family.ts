export type FamilyMember = {
    id: string;
    name: string;
    role: string;
    theme?: string;
    backgroundImageUrl?: string;
};

export type FamilyResponse = {
    id: string;
    name: string;
};

export type FamilyDetails = {
    id: string;
    name: string;
    members: FamilyMember[];
};
