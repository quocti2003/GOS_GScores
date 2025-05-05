const registrationForm = document.getElementById('registration-form');
const registrationNumberInput = document.getElementById('registrationNumberInput');
const registrationNumberError = document.getElementById('registrationNumberError');
const submitBtn = document.getElementById('submitBtn');
const detailedScoresContent = document.getElementById('detailedScoresContent');
const detailedScoresLoading = document.getElementById('detailedScoresLoading');
const detailedScoresError = document.getElementById('detailedScoresError');
const dashboardContent = document.getElementById('dashboardContent');
const dashboardLoading = document.getElementById('dashboardLoading');
const dashboardError = document.getElementById('dashboardError');
const reportsContent = document.getElementById('reportsContent');
const reportsLoading = document.getElementById('reportsLoading');
const reportsError = document.getElementById('reportsError');
const navLinks = document.querySelectorAll('.nav-link');
const sections = document.querySelectorAll('main section');

// --- Helper Functions ---
function displayError(errorDiv, message) {
    errorDiv.textContent = message;
    errorDiv.classList.remove('d-none');
}

function hideError(errorDiv) {
    errorDiv.textContent = '';
    errorDiv.classList.add('d-none');
}

function displayValidationError(inputElement, errorElement, message) {
    inputElement.classList.add('is-invalid');
    if (errorElement) errorElement.textContent = message;
}

function clearValidationError(inputElement) {
    inputElement.classList.remove('is-invalid');
}

function showSection(sectionId) {
    sections.forEach(section => {
        section.classList.add('d-none');
        if (section.id === sectionId) {
            section.classList.remove('d-none');
        }
    });
    navLinks.forEach(link => {
        link.classList.remove('active', 'fw-bold');
        if (link.dataset.section === sectionId) {
            link.classList.add('active', 'fw-bold');
        }
    });
}

// --- Load Dashboard (Top 10 Khối A) ---
async function loadDashboard() {
    hideError(dashboardError);
    dashboardContent.innerHTML = '<p class="text-muted">Đang tải dữ liệu...</p>';
    dashboardLoading.classList.remove('d-none');

    try {
        const response = await fetch('/api/scores/top10');
        if (!response.ok) throw new Error('Không thể tải top 10 điểm khối A');
        const data = await response.json();

        if (data.length === 0) {
            dashboardContent.innerHTML = '<p class="text-muted">Không có dữ liệu top 10.</p>';
            return;
        }

        dashboardContent.innerHTML = `
            <div class="table-responsive">
                <table class="table table-striped table-bordered">
                    <thead>
                        <tr>
                            <th>SBD</th>
                            <th>Toán</th>
                            <th>Vật Lý</th>
                            <th>Hóa Học</th>
                            <th>Tổng Điểm</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${data.map(student => `
                            <tr>
                                <td>${student.sbd}</td>
                                <td>${student.toan || '-'}</td>
                                <td>${student.vatLi || '-'}</td>
                                <td>${student.hoaHoc || '-'}</td>
                                <td>${student.total}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    } catch (error) {
        displayError(dashboardError, `Lỗi: ${error.message}`);
        dashboardContent.innerHTML = '<p class="text-muted">Danh sách top 10 học sinh có điểm khối A cao nhất sẽ hiển thị tại đây.</p>';
    } finally {
        dashboardLoading.classList.add('d-none');
    }
}

// --- Load Search Scores ---
async function searchScores(regNumber) {
    hideError(detailedScoresError);
    detailedScoresContent.innerHTML = '<p class="text-muted">Đang tải kết quả...</p>';
    detailedScoresLoading.classList.remove('d-none');

    try {
        const response = await fetch(`/api/scores/${regNumber}`);
        if (!response.ok) throw new Error(response.status === 404 ? 'Không tìm thấy số báo danh' : 'Lỗi khi tra cứu');
        const data = await response.json();
        // <li class="list-group-item d-flex justify-content-between"><span>Ngoại Ngữ:</span> <strong>${data.ngoaiNgu || '-'} (${data.maNgoaiNgu || '-'})</strong></li>
        detailedScoresContent.innerHTML = `
            <p>Kết quả cho SBD: <strong>${regNumber}</strong></p>
            <ul class="list-group list-group-flush">
                <li class="list-group-item d-flex justify-content-between"><span>Toán:</span> <strong>${data.toan || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>Ngữ Văn:</span> <strong>${data.nguVan || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>Ngoại Ngữ:</span> <strong>${data.ngoaiNgu || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>Mã Ngoại Ngữ:</span> <strong>${data.maNgoaiNgu || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>Vật Lý:</span> <strong>${data.vatLi || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>Hóa Học:</span> <strong>${data.hoaHoc || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>Sinh Học:</span> <strong>${data.sinhHoc || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>Lịch Sử:</span> <strong>${data.lichSu || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>Địa Lý:</span> <strong>${data.diaLi || '-'}</strong></li>
                <li class="list-group-item d-flex justify-content-between"><span>GDCD:</span> <strong>${data.gdcd || '-'}</strong></li>
            </ul>
        `;
    } catch (error) {
        displayError(detailedScoresError, `Lỗi: ${error.message}`);
        detailedScoresContent.innerHTML = '<p class="text-muted">Detailed view of search scores here.</p>';
    } finally {
        detailedScoresLoading.classList.add('d-none');
    }
}

// --- Load Reports (Statistics Chart) ---
let scoreChart = null;

async function loadReports() {
    hideError(reportsError);
    reportsContent.innerHTML = '<p class="text-muted">Đang tải thống kê...</p>';
    reportsLoading.classList.remove('d-none');

    try {
        const response = await fetch('/api/scores/statistics');
        if (!response.ok) throw new Error('Không thể tải dữ liệu thống kê');
        const data = await response.json();

        if (data.length === 0) {
            reportsContent.innerHTML = '<p class="text-muted">Không có dữ liệu thống kê.</p>';
            return;
        }

        reportsContent.innerHTML = '<canvas id="scoreDistributionChart"></canvas>';
        const ctx = document.getElementById('scoreDistributionChart').getContext('2d');

        if (scoreChart) scoreChart.destroy();

        const subjects = data.map(item => item.subject);
        const datasets = [
            {
                label: '>=8',
                data: data.map(item => item.distribution['>=8']),
                backgroundColor: 'rgba(40, 167, 69, 0.7)',
            },
            {
                label: '6-8',
                data: data.map(item => item.distribution['6-8']),
                backgroundColor: 'rgba(0, 123, 255, 0.7)',
            },
            {
                label: '4-6',
                data: data.map(item => item.distribution['4-6']),
                backgroundColor: 'rgba(255, 193, 7, 0.7)',
            },
            {
                label: '<4',
                data: data.map(item => item.distribution['<4']),
                backgroundColor: 'rgba(220, 53, 69, 0.7)',
            }
        ];

        scoreChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: subjects,
                datasets: datasets
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'top' },
                    title: {
                        display: true,
                        text: 'Phân Bố Điểm Theo Môn Học'
                    }
                },
                scales: {
                    x: { stacked: true },
                    y: {
                        stacked: true,
                        beginAtZero: true,
                        title: { display: true, text: 'Số Lượng Học Sinh' }
                    }
                }
            }
        });
    } catch (error) {
        displayError(reportsError, `Lỗi: ${error.message}`);
        reportsContent.innerHTML = '<canvas id="scoreDistributionChart"></canvas>';
    } finally {
        reportsLoading.classList.add('d-none');
    }
}

// --- Event Listeners ---
if (registrationForm) {
    registrationForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        clearValidationError(registrationNumberInput);
        hideError(detailedScoresError);
        detailedScoresContent.innerHTML = '';
        detailedScoresLoading.classList.remove('d-none');
        submitBtn.disabled = true;

        const regNumber = registrationNumberInput.value.trim();

        if (!/^\d{7,12}$/.test(regNumber)) {
            displayValidationError(registrationNumberInput, registrationNumberError, 'Số báo danh phải có 7-12 chữ số.');
            detailedScoresLoading.classList.add('d-none');
            submitBtn.disabled = false;
            return;
        }

        await searchScores(regNumber);
        submitBtn.disabled = false;
    });
}

navLinks.forEach(link => {
    link.addEventListener('click', (event) => {
        event.preventDefault();
        const sectionId = link.dataset.section;
        showSection(sectionId);

        if (sectionId === 'dashboard') {
            loadDashboard();
        } else if (sectionId === 'reports') {
            loadReports();
        } else if (sectionId === 'search-scores') {
            registrationForm.reset();
            clearValidationError(registrationNumberInput);
            hideError(detailedScoresError);
            detailedScoresContent.innerHTML = '<p class="text-muted">Kết quả tra cứu điểm sẽ hiển thị tại đây.</p>';
        }
    });
});

// --- Initial Setup ---
document.addEventListener('DOMContentLoaded', () => {
    console.log("Dashboard DOM loaded.");
    showSection('dashboard');
    loadDashboard();
});