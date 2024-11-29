import sqlite3
import requests

def download_image_as_binary(url):
    response = requests.get(url)
    if response.status_code == 200:
        return response.content
    else:
        print(f"Failed to download image: {url}")
        return None

def update_links_to_blob(db_path):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    cursor.execute("SELECT uid, img FROM recipe")
    rows = cursor.fetchall()

    for row in rows:
        uid, img_link = row
        print(f"Processing UID {uid} with link {img_link}...")

        binary_data = download_image_as_binary(img_link)
        if binary_data:
            cursor.execute("UPDATE recipe SET img = ? WHERE uid = ?", (binary_data, uid))
    
    conn.commit()
    conn.close()
    print("All image links have been converted to BLOBs.")

def main():
    db_path = "/mnt/data/recipe.db"

    update_links_to_blob(db_path)

if __name__ == "__main__":
    main()
