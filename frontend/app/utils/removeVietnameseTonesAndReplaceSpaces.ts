export function removeVietnameseTonesAndReplaceSpaces(str: string) {
    const vietnameseChars = [
        'à', 'á', 'ả', 'ã', 'ạ', 'â', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ',
        'ă', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ', 'è', 'é', 'ẻ', 'ẽ', 'ẹ', 'ê', 'ế', 'ề', 'ể', 'ễ', 'ệ',
        'ì', 'í', 'ỉ', 'ĩ', 'ị', 'ò', 'ó', 'ỏ', 'õ', 'ọ', 'ô', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ',
        'ơ', 'ớ', 'ờ', 'ở', 'ỡ', 'ợ', 'ù', 'ú', 'ủ', 'ũ', 'ụ', 'ư', 'ứ', 'ừ', 'ử', 'ữ', 'ự',
        'ỳ', 'ý', 'ỷ', 'ỹ', 'ỵ', 'Đ', 'đ'
    ];

    const noTonesChars = [
        'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a',
        'a', 'a', 'a', 'a', 'a', 'a', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e', 'e',
        'i', 'i', 'i', 'i', 'i', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o', 'o',
        'o', 'o', 'o', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u', 'u',
        'y', 'y', 'y', 'y', 'y', 'd', 'd'
    ];

    // Chuyển đổi tiếng Việt có dấu sang không dấu
    const noTonesStr = str.split('').map(char => {
        const index = vietnameseChars.indexOf(char);
        return index !== -1 ? noTonesChars[index] : char;
    }).join('');

    // Thay thế khoảng trắng và dấu gạch chéo bằng dấu gạch ngang
    let result = noTonesStr.replace(/[\s/]+/g, '-'); // Thay tất cả khoảng trắng và dấu /

    // Xóa khoảng trắng ở đầu và cuối chuỗi
    result = result.trim();

    // Xóa dấu gạch ngang ở đầu và cuối chuỗi (nếu có)
    result = result.replace(/^-|-$/g, '');

    // Xóa dấu gạch ngang liền kề thành một dấu gạch ngang duy nhất
    result = result.replace(/-{2,}/g, '-');

    return result.toLowerCase();
}