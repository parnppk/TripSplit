# โพย OOP – Trip Split

## ประโยคตั้งตัว (ลืมหมดก็พูดอันนี้)
> `Expense` เป็นแม่แบบ abstract ลูก 4 ตัว extends ไป ข้อมูลเป็น private และแต่ละตัวคิด `getTotal()` คนละสูตร

---

## ทางลัด VS Code
| กด | ทำอะไร |
|---|---|
| `Ctrl` + คลิกชื่อคลาส | ไปไฟล์ต้นทาง (หาว่า extends/implements มาจากไหน) |
| `Ctrl + P` | เปิดไฟล์จากชื่อ |
| `Ctrl + G` | ไปบรรทัดที่ต้องการ |
| `Ctrl + Shift + F` | ค้นคำทั้งโปรเจกต์ |

---

## extends (เป็นลูกของใคร)
| คลาส | extends |
|---|---|
| FoodExpense, TransportExpense, HotelExpense, ActivityExpense | **Expense** |
| MemberPanel, ExpenseFormPanel, ExpenseListPanel, SummaryPanel | **TripCard** |
| ExpenseTableModel, BalanceTableModel | **TripTableModel** |
| TripCard, StatCard | RoundedPanel |
| TripSplitForm | JFrame (ของ Java) |

## implements (ทำตามข้อตกลงของใคร)
| คลาส | implements |
|---|---|
| Expense | **Splittable** |
| TripCard, StatsPanel | **Refreshable** |

## @Override (เขียนทับเมธอดของใคร)
| เมธอด | อยู่ใน | มาจาก |
|---|---|---|
| getTotal(), getCategory(), getDetail() | ลูก 4 ตัว | Expense (abstract ไม่มีสูตร) |
| getShareFor() | ActivityExpense | Expense (หารเท่ากัน) |
| getTotal(), getShareFor(), involves() | Expense | Splittable (interface) |
| refresh() | การ์ดทุกใบ | Refreshable (interface) |
| equals(), hashCode(), toString() | Member | Object (แม่ใหญ่ของทุกคลาส) |
| paintComponent() | ปุ่ม การ์ด กราฟ | Swing (JComponent) |

**หลักจำ:** @Override มาจากคลาสที่อยู่หลัง `extends` หรือ `implements` ที่หัวไฟล์

---

## โดนถามเรื่องไหน → เปิดไฟล์ไหน
| เรื่อง | ไฟล์ : บรรทัด | พูดว่า |
|---|---|---|
| Abstraction | Expense.java : 23 | abstract class สร้าง object ไม่ได้ บอกว่าต้องคิดยอดได้ แต่ไม่บอกสูตร |
| Interface | Splittable.java : 13 | ข้อตกลงว่าต้องมีเมธอดอะไร ไม่มีโค้ดข้างใน |
| Encapsulation | Expense.java : 26 | field เป็น private + ตรวจข้อมูลตอนสร้าง |
| Inheritance | FoodExpense.java : 15 | extends Expense ได้ชื่อ ผู้จ่าย ผู้ร่วมหาร จากแม่ |
| Override | ActivityExpense.java : 75 | แม่หารเท่ากัน ค่ากิจกรรมเขียนทับให้จ่ายตามราคาตัวเอง |
| Polymorphism | Trip.java : 201 | เรียก getTotal() คำเดียว แต่ละประเภทคิดคนละสูตร |
| Polymorphism (UI) | TripSplitForm.java : 294 | เรียก refresh() ในลูป การ์ดแต่ละใบวาดคนละแบบ |
| คำนวณโอนเงิน | Trip.java : 272 | แบ่งได้คืน/ต้องจ่าย → เรียงยอดเยอะสุด → จับคู่ทีละคู่ |

---

## เห็นคำนี้ → แปลว่า
| คำ | แปลว่า |
|---|---|
| private | ข้างนอกแตะไม่ได้ (Encapsulation) |
| protected | ลูกใช้ได้ คนนอกใช้ไม่ได้ |
| abstract | ยังไม่สมบูรณ์ ลูกต้องเขียนต่อ |
| interface | ข้อตกลง มีแค่ชื่อเมธอด |
| extends | เป็นลูกของ (Inheritance) |
| implements | ทำตามข้อตกลงของ interface |
| super(...) | เรียกแม่ให้ช่วยทำ |
| @Override | เขียนเมธอดของแม่ใหม่ (ทำให้เกิด Polymorphism) |
| final | ห้ามเปลี่ยนค่า / ห้ามลูกเขียนทับ |
| static | ของคลาส ไม่ต้องสร้าง object |
| throw new ...Exception | ข้อมูลผิด หยุดแล้วแจ้งเตือน |

**อ่านไม่ออก → ดูคอมเมนต์ `// # ...` เหนือโค้ด มีคำอธิบายภาษาไทยทุกส่วน**
