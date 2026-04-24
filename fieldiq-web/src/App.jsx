import { useState, useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from 'react-leaflet'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl:       'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl:     'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

function stopIcon(n, done) {
  return L.divIcon({
    className: '',
    html: `<div style="background:${done ? '#10b981' : '#4f46e5'};color:#fff;width:30px;height:30px;
      border-radius:50%;display:flex;align-items:center;justify-content:center;
      font-weight:800;font-size:12px;border:2.5px solid #fff;
      box-shadow:0 2px 8px rgba(0,0,0,0.25);font-family:Inter,sans-serif">
      ${done ? '✓' : n}</div>`,
    iconSize: [30, 30], iconAnchor: [15, 15],
  })
}

const homeIcon = L.divIcon({
  className: '',
  html: `<div style="background:#f59e0b;color:#fff;width:34px;height:34px;border-radius:10px;
    display:flex;align-items:center;justify-content:center;font-size:17px;
    border:2.5px solid #fff;box-shadow:0 2px 8px rgba(0,0,0,0.25)">🏠</div>`,
  iconSize: [34, 34], iconAnchor: [17, 17],
})

function FitBounds({ points }) {
  const map = useMap()
  useEffect(() => {
    if (points.length > 1) map.fitBounds(points, { padding: [40, 40] })
    else if (points.length === 1) map.setView(points[0], 14)
  }, [points.length])
  return null
}

const API     = 'https://satchel-cancel-abnormal.ngrok-free.dev'
const HEADERS = { 'Content-Type': 'application/json', 'ngrok-skip-browser-warning': 'true' }

function authHeaders() {
  const s = JSON.parse(localStorage.getItem('fiq_session') || '{}')
  const h = { ...HEADERS }
  if (s.token) h['Authorization'] = `Bearer ${s.token}`
  return h
}

const AVATAR_BG = ['#4f46e5','#10b981','#f59e0b','#ef4444','#8b5cf6','#0ea5e9','#06b6d4','#ec4899']

function initials(name) {
  return name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase()
}

function avatarBg(name) {
  let h = 0
  for (let c of name) h = (h * 31 + c.charCodeAt(0)) & 0xffffffff
  return AVATAR_BG[Math.abs(h) % AVATAR_BG.length]
}

function greeting() {
  const h = new Date().getHours()
  if (h < 12) return 'Good morning'
  if (h < 17) return 'Good afternoon'
  return 'Good evening'
}

function todayLabel() {
  return new Date().toLocaleDateString('en-IN', { weekday: 'long', day: 'numeric', month: 'short' })
}

function fmt(amount) {
  if (!amount) return '—'
  return '₹' + Number(amount).toLocaleString('en-IN')
}

function dpdColor(dpd) {
  if (dpd >= 90) return '#ef4444'
  if (dpd >= 60) return '#f59e0b'
  return 'inherit'
}

function statusChip(status) {
  const m = {
    PENDING:     ['chip chip-pending',  'Pending'],
    IN_PROGRESS: ['chip chip-progress', 'In Progress'],
    COMPLETED:   ['chip chip-done',     'Completed'],
    SKIPPED:     ['chip chip-skipped',  'Skipped'],
  }
  const [cls, label] = m[status] || ['chip', status]
  return <span className={cls}>{label}</span>
}

function fmtWindow(start, end) {
  if (!start) return null
  return `${start}${end ? ` – ${end}` : ''}`
}

const OUTCOME_META = {
  PAID_FULL:               { icon: '💰', label: 'Paid in Full' },
  PAID_PARTIAL:            { icon: '💵', label: 'Partial Payment' },
  PTP:                     { icon: '🤝', label: 'Promise to Pay' },
  NOT_HOME:                { icon: '🚪', label: 'Not Home' },
  REFUSED:                 { icon: '🚫', label: 'Refused' },
  WRONG_ADDRESS:           { icon: '📍', label: 'Wrong Address' },
  ALREADY_SETTLED:         { icon: '✅', label: 'Already Settled' },
  CONTACT_LATER:           { icon: '📞', label: 'Contact Later' },
  LEGAL_NOTICE_DELIVERED:  { icon: '📋', label: 'Notice Delivered' },
  DECEASED:                { icon: '🕊️', label: 'Deceased' },
}

function Spinner({ text = 'Loading…' }) {
  return (
    <div className="loading-wrap">
      <div className="spinner" />
      <div className="loading-text">{text}</div>
    </div>
  )
}

// ─── Screen 0: Login ──────────────────────────────────────────────────────────

function LoginScreen({ onLogin }) {
  const [phone, setPhone]       = useState('')
  const [pin, setPin]           = useState('')
  const [loading, setLoading]   = useState(false)
  const [error, setError]       = useState('')

  const submit = async (e) => {
    e.preventDefault()
    if (!phone || !pin) return setError('Enter phone and PIN')
    setLoading(true)
    setError('')
    try {
      const res = await fetch(`${API}/api/auth/login`, {
        method: 'POST', headers: HEADERS,
        body: JSON.stringify({ phone, pin }),
      })
      if (!res.ok) {
        const data = await res.json().catch(() => ({}))
        setError(data.error || 'Invalid phone or PIN')
        setLoading(false)
        return
      }
      const session = await res.json()
      localStorage.setItem('fiq_session', JSON.stringify(session))
      onLogin(session)
    } catch {
      setError('Cannot reach server. Check your connection.')
      setLoading(false)
    }
  }

  return (
    <div className="screen">
      <div className="topbar">
        <div className="topbar-row">
          <div className="app-logo">⚡</div>
          <div>
            <div className="topbar-title">FieldIQ</div>
            <div className="topbar-sub">Collections Intelligence</div>
          </div>
        </div>
      </div>

      <div className="content">
        <div className="hero">
          <div className="hero-greeting">{greeting()}</div>
          <div className="hero-title">Sign in to start your day</div>
          <div className="hero-sub">{todayLabel()}</div>
        </div>

        <form onSubmit={submit}>
          <div className="form-group">
            <label className="form-label">Mobile Number</label>
            <input
              className="form-input"
              type="tel"
              placeholder="e.g. 9820001111"
              value={phone}
              onChange={e => setPhone(e.target.value)}
              autoComplete="tel"
            />
          </div>
          <div className="form-group">
            <label className="form-label">PIN</label>
            <input
              className="form-input"
              type="password"
              placeholder="4-digit PIN"
              maxLength={4}
              value={pin}
              onChange={e => setPin(e.target.value)}
              autoComplete="current-password"
            />
          </div>

          {error && (
            <div className="alert alert-danger" style={{ marginBottom: 16 }}>
              ⚠️ {error}
            </div>
          )}

          <div className="btn-wrap" style={{ padding: 0 }}>
            <button className="btn btn-primary" type="submit" disabled={loading}>
              {loading ? 'Signing in…' : '→ Sign In'}
            </button>
          </div>
        </form>

        <div className="card no-tap" style={{ marginTop: 24, background: 'var(--bg2)' }}>
          <div className="section-title" style={{ marginBottom: 10 }}>Demo Credentials</div>
          {[
            ['Rajesh Kumar', '9820001111', '1111', 'South Mumbai'],
            ['Priya Sharma',  '9820002222', '2222', 'Bandra West'],
            ['Anil Patil',    '9820003333', '3333', 'Andheri East'],
            ['Sunita Desai',  '9820004444', '4444', 'Thane'],
            ['M. Shaikh',     '9820005555', '5555', 'Navi Mumbai'],
          ].map(([name, ph, p, zone]) => (
            <div key={ph} className="detail-row" style={{ cursor: 'pointer' }}
              onClick={() => { setPhone(ph); setPin(p) }}>
              <span className="detail-lbl">{name}</span>
              <span className="detail-val" style={{ color: 'var(--t2)', fontSize: 12 }}>
                {ph} · PIN {p} · {zone}
              </span>
            </div>
          ))}
          <div style={{ fontSize: 11, color: 'var(--t3)', marginTop: 8 }}>Tap a row to auto-fill</div>
        </div>
      </div>
    </div>
  )
}

// ─── Screen 1: Today's Route ──────────────────────────────────────────────────

function RouteList({ agent, preloadedVisits, onVisitClick, onLogout }) {
  const [visits, setVisits]   = useState(preloadedVisits ?? [])
  const [loading, setLoading] = useState(preloadedVisits === null)
  const [generating, setGen]  = useState(false)
  const [view, setView]       = useState('list')
  const today = new Date().toISOString().split('T')[0]

  const load = () => {
    setLoading(true)
    fetch(`${API}/api/routes/agent/${agent.agentId}?date=${today}`, { headers: authHeaders() })
      .then(r => r.json())
      .then(data => { setVisits(Array.isArray(data) ? data : []); setLoading(false) })
      .catch(() => setLoading(false))
  }

  const generateRoutes = () => {
    setGen(true)
    fetch(`${API}/api/routes/generate`, {
      method: 'POST', headers: authHeaders(),
      body: JSON.stringify({ date: today }),
    })
      .then(r => r.json())
      .then(() => { setGen(false); load() })
      .catch(() => setGen(false))
  }

  useEffect(() => {
    if (preloadedVisits !== null) { setVisits(preloadedVisits); setLoading(false) }
    else load()
  }, [agent.agentId])

  const done  = visits.filter(v => v.status === 'COMPLETED').length
  const total = visits.length
  const pct   = total > 0 ? Math.round((done / total) * 100) : 0
  const totalDist = visits.reduce((s, v) => s + (v.distanceFromPrevious || 0), 0).toFixed(1)

  const homePos  = [agent.homeLatitude ?? 18.98, agent.homeLongitude ?? 72.83]
  const routeLine = [homePos, ...visits.map(v => [v.latitude, v.longitude])].filter(p => p[0] && p[1])

  return (
    <div className="screen">
      <div className="topbar">
        <div className="topbar-row">
          <div className="agent-avatar" style={{ background: avatarBg(agent.name), width: 36, height: 36, fontSize: 13, borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontWeight: 700, flexShrink: 0 }}>
            {initials(agent.name)}
          </div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div className="topbar-title">{agent.name}</div>
            <div className="topbar-sub">{agent.zone} · {todayLabel()}</div>
          </div>
          <button className="btn-ghost-sm" onClick={onLogout} title="Sign out" style={{ flexShrink: 0, background: 'transparent', border: 'none', cursor: 'pointer', color: 'var(--t2)', fontSize: 20, padding: '4px 8px' }}>⏏</button>
        </div>
      </div>

      {loading && <Spinner text="Loading route…" />}

      {!loading && total === 0 && (
        <div className="content">
          <div className="empty-wrap">
            <div className="empty-icon">🗺️</div>
            <div className="empty-title">No route yet</div>
            <div className="empty-sub">Today's visit route hasn't been generated. Tap below to optimise and assign visits.</div>
            <button className="btn btn-primary" onClick={generateRoutes} disabled={generating}>
              {generating ? 'Generating route…' : '⚡ Generate Today\'s Route'}
            </button>
          </div>
        </div>
      )}

      {!loading && total > 0 && (
        <>
          <div className="content" style={{ paddingBottom: 0 }}>
            <div className="progress-wrap">
              <div className="progress-header">
                <span className="progress-label">Today's progress</span>
                <span className="progress-count">{done} / {total} visits</span>
              </div>
              <div className="progress-track">
                <div className="progress-fill" style={{ width: `${pct}%` }} />
              </div>
            </div>

            <div className="stats-row">
              <div className="stat-box">
                <span className="stat-icon">📍</span>
                <div>
                  <div className="stat-val">{totalDist} <span style={{ fontSize: 12, fontWeight: 600 }}>km</span></div>
                  <div className="stat-lbl">Total distance</div>
                </div>
              </div>
              <div className="stat-box">
                <span className="stat-icon">✅</span>
                <div>
                  <div className="stat-val">{pct}<span style={{ fontSize: 12, fontWeight: 600 }}>%</span></div>
                  <div className="stat-lbl">Completion</div>
                </div>
              </div>
            </div>

            <div className="toggle-tabs">
              {['list', 'map'].map(t => (
                <button key={t} className={`toggle-tab${view === t ? ' active' : ''}`} onClick={() => setView(t)}>
                  {t === 'map' ? '🗺 Map' : '☰ List'}
                </button>
              ))}
            </div>
          </div>

          {view === 'map' && (
            <div style={{ position: 'relative', flex: 1 }}>
              <div className="map-wrap">
                <MapContainer center={homePos} zoom={13} style={{ height: '100%', width: '100%' }}>
                  <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" attribution='© OpenStreetMap' />
                  <FitBounds points={routeLine} />
                  <Polyline positions={routeLine} color="#4f46e5" weight={3} opacity={0.65} dashArray="6,5" />
                  <Marker position={homePos} icon={homeIcon}>
                    <Popup><b>{agent.name}</b><br />Starting point</Popup>
                  </Marker>
                  {visits.map(v => v.latitude && v.longitude ? (
                    <Marker key={v.visitId} position={[v.latitude, v.longitude]}
                      icon={stopIcon(v.routeOrder, v.status === 'COMPLETED')}
                      eventHandlers={{ click: () => onVisitClick(v) }}>
                      <Popup>
                        <b>{v.borrowerName}</b><br />
                        {v.address}<br />
                        <span style={{ color: dpdColor(v.dpd) }}>DPD: {v.dpd} days</span> · {fmt(v.outstandingAmount)}
                        {v.visitWindowStart && <><br />🕐 Available {fmtWindow(v.visitWindowStart, v.visitWindowEnd)}</>}
                      </Popup>
                    </Marker>
                  ) : null)}
                </MapContainer>
              </div>
              <div className="map-hint">Tap a pin to view details</div>
            </div>
          )}

          {view === 'list' && (
            <div className="content">
              {visits.map(v => {
                const window = fmtWindow(v.visitWindowStart, v.visitWindowEnd)
                return (
                  <div key={v.visitId} className="card" onClick={() => onVisitClick(v)}>
                    <div className="visit-row">
                      <div className="stop-num" style={{
                        background: v.status === 'COMPLETED' ? '#10b981'
                          : v.visitWindowStart ? '#f59e0b' : '#4f46e5'
                      }}>
                        {v.status === 'COMPLETED' ? '✓' : v.routeOrder}
                      </div>
                      <div className="visit-body">
                        <div className="visit-name">{v.borrowerName}</div>
                        <div className="visit-addr">{v.address}</div>
                        {window && (
                          <div style={{ fontSize: 12, color: '#92400e', fontWeight: 600, marginTop: 4 }}>
                            🕐 Available {window}
                          </div>
                        )}
                        <div className="chips-row">
                          {statusChip(v.status)}
                          {v.hasPendingLegalNotice && <span className="chip chip-legal">⚖️ Legal</span>}
                          {v.dpd > 0 && <span className="chip chip-dpd" style={{ color: dpdColor(v.dpd) }}>DPD {v.dpd}d</span>}
                          {v.distanceFromPrevious != null && (
                            <span className="chip chip-dist">{v.distanceFromPrevious} km</span>
                          )}
                        </div>
                      </div>
                      <div className="chevron">›</div>
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </>
      )}
    </div>
  )
}

// ─── Screen 2: Visit Detail ───────────────────────────────────────────────────

function VisitDetail({ visit: init, agent, onBack, onOutcome, onRouteUpdated }) {
  const [visit, setVisit]         = useState(init)
  const [starting, setStarting]   = useState(false)
  const [deferring, setDeferring] = useState(false)
  const [showDefer, setShowDefer] = useState(false)
  const [winStart, setWinStart]   = useState('')
  const [winEnd, setWinEnd]       = useState('')

  const startVisit = async () => {
    setStarting(true)
    const res = await fetch(`${API}/api/visits/${visit.visitId}/start`, { method: 'PUT', headers: authHeaders() })
    if (res.ok) setVisit(await res.json())
    setStarting(false)
  }

  const callReoptimise = async (lat, lng) => {
    const res = await fetch(`${API}/api/routes/reoptimise`, {
      method: 'PUT', headers: authHeaders(),
      body: JSON.stringify({
        agentId: agent.agentId, date: new Date().toISOString().split('T')[0],
        deferVisitId: visit.visitId, currentLat: lat, currentLng: lng,
        visitWindowStart: winStart || null, visitWindowEnd: winEnd || null,
      }),
    })
    setDeferring(false)
    if (res.ok) onRouteUpdated(await res.json())
    else alert('Could not reoptimise. Try again.')
  }

  const deferVisit = () => {
    setDeferring(true)
    navigator.geolocation.getCurrentPosition(
      p => callReoptimise(p.coords.latitude, p.coords.longitude),
      () => callReoptimise(agent.homeLatitude ?? 18.98, agent.homeLongitude ?? 72.83),
      { timeout: 8000 }
    )
  }

  const canStart = visit.status === 'PENDING'
  const canLog   = visit.status === 'IN_PROGRESS' || visit.status === 'PENDING'
  const isDone   = visit.status === 'COMPLETED' || visit.status === 'SKIPPED'

  return (
    <div className="screen">
      <div className="topbar">
        <div className="topbar-row">
          <button className="back-btn" onClick={onBack}>‹</button>
          <div style={{ flex: 1 }}>
            <div className="topbar-title">{visit.borrowerName}</div>
            <div className="topbar-sub" style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
              Stop #{visit.routeOrder} · {statusChip(visit.status)}
            </div>
          </div>
        </div>
      </div>

      <div className="content">
        {visit.hasPendingLegalNotice && (
          <div className="alert alert-danger">⚠️ Legal notice must be served today</div>
        )}
        {visit.visitWindowStart && (
          <div className="alert alert-warning">
            🕐 Borrower available {visit.visitWindowStart} – {visit.visitWindowEnd || '…'}
          </div>
        )}

        <div className="card no-tap">
          <div className="section-title">Borrower Details</div>
          <div className="detail-row">
            <span className="detail-lbl">Name</span>
            <span className="detail-val">{visit.borrowerName}</span>
          </div>
          <div className="detail-row">
            <span className="detail-lbl">Phone</span>
            <span className="detail-val">{visit.borrowerPhone}</span>
          </div>
          <div className="detail-row">
            <span className="detail-lbl">Address</span>
            <span className="detail-val">{visit.address}</span>
          </div>
          <div className="detail-row">
            <span className="detail-lbl">Outstanding</span>
            <span className="detail-val" style={{ color: '#4f46e5', fontSize: 15 }}>{fmt(visit.outstandingAmount)}</span>
          </div>
          <div className="detail-row">
            <span className="detail-lbl">Days Past Due</span>
            <span className="detail-val dpd-high" style={{ color: dpdColor(visit.dpd) }}>{visit.dpd} days</span>
          </div>
          {visit.brokenPtpCount > 0 && (
            <div className="detail-row">
              <span className="detail-lbl">Broken Promises</span>
              <span className="detail-val" style={{ color: '#ef4444' }}>{visit.brokenPtpCount}</span>
            </div>
          )}
        </div>

        {isDone && visit.outcomeType && (
          <div className="card no-tap">
            <div className="outcome-badge">✓ Visit Completed</div>
            <div className="detail-row">
              <span className="detail-lbl">Result</span>
              <span className="detail-val">
                {OUTCOME_META[visit.outcomeType]?.icon} {OUTCOME_META[visit.outcomeType]?.label || visit.outcomeType.replace(/_/g,' ')}
              </span>
            </div>
            {visit.amountCollected && (
              <div className="detail-row">
                <span className="detail-lbl">Amount Collected</span>
                <span className="detail-val" style={{ color: '#10b981' }}>{fmt(visit.amountCollected)}</span>
              </div>
            )}
            {visit.ptpDate && (
              <div className="detail-row">
                <span className="detail-lbl">PTP Date</span>
                <span className="detail-val">{visit.ptpDate}</span>
              </div>
            )}
            {visit.notes && (
              <div className="detail-row">
                <span className="detail-lbl">Notes</span>
                <span className="detail-val">{visit.notes}</span>
              </div>
            )}
          </div>
        )}

        {!isDone && showDefer && (
          <div className="card no-tap">
            <div className="section-title">Borrower's available time</div>
            <div style={{ display: 'flex', gap: 10, marginBottom: 12 }}>
              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label className="form-label">From</label>
                <input className="form-input" type="time" value={winStart} onChange={e => setWinStart(e.target.value)} />
              </div>
              <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                <label className="form-label">To</label>
                <input className="form-input" type="time" value={winEnd} onChange={e => setWinEnd(e.target.value)} />
              </div>
            </div>
            <p style={{ fontSize: 12, color: 'var(--t2)', marginBottom: 14, lineHeight: 1.5 }}>
              Leave blank to move this visit to end of day. With a window, the route will be resequenced to arrive during the borrower's slot.
            </p>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn btn-primary" style={{ flex: 1 }} onClick={deferVisit} disabled={deferring}>
                {deferring ? 'Reoptimising…' : '🔄 Confirm & Reoptimise'}
              </button>
              <button className="btn btn-ghost" style={{ flex: 0, padding: '15px 18px' }} onClick={() => setShowDefer(false)}>✕</button>
            </div>
          </div>
        )}
      </div>

      {!isDone && (
        <div className="btn-wrap">
          {canStart && (
            <button className="btn btn-outline" onClick={startVisit} disabled={starting}>
              {starting ? 'Starting…' : '▶ Start Visit'}
            </button>
          )}
          {canLog && (
            <button className="btn btn-primary" onClick={() => onOutcome(visit)}>
              📋 Log Outcome
            </button>
          )}
          {!showDefer && (
            <button className="btn btn-warn" onClick={() => setShowDefer(true)}>
              🔄 Borrower Unavailable / Reschedule
            </button>
          )}
        </div>
      )}
    </div>
  )
}

// ─── Screen 3: Log Outcome ────────────────────────────────────────────────────

function LogOutcome({ visit, onBack, onDone }) {
  const [outcome, setOutcome]       = useState('')
  const [amount, setAmount]         = useState('')
  const [ptpDate, setPtpDate]       = useState('')
  const [notes, setNotes]           = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [gpsStatus, setGpsStatus]   = useState('idle')
  const [coords, setCoords]         = useState(null)

  const getGPS = () => {
    setGpsStatus('getting')
    navigator.geolocation.getCurrentPosition(
      p => { setCoords({ lat: p.coords.latitude, lng: p.coords.longitude }); setGpsStatus('got') },
      ()  => setGpsStatus('error'),
      { timeout: 10000 }
    )
  }

  const submit = async () => {
    if (!outcome) return alert('Please select an outcome')
    setSubmitting(true)
    const res = await fetch(`${API}/api/visits/${visit.visitId}/outcome`, {
      method: 'POST', headers: authHeaders(),
      body: JSON.stringify({
        outcome,
        notes:           notes || null,
        amountCollected: amount ? parseFloat(amount) : null,
        ptpDate:         ptpDate || null,
        visitLatitude:   coords?.lat ?? null,
        visitLongitude:  coords?.lng ?? null,
      }),
    })
    if (res.ok) onDone(await res.json())
    else alert('Failed to submit. Try again.')
    setSubmitting(false)
  }

  const needsAmount = ['PAID_FULL', 'PAID_PARTIAL'].includes(outcome)
  const needsPtp    = outcome === 'PTP'

  return (
    <div className="screen">
      <div className="topbar">
        <div className="topbar-row">
          <button className="back-btn" onClick={onBack}>‹</button>
          <div>
            <div className="topbar-title">Log Outcome</div>
            <div className="topbar-sub">{visit.borrowerName} · {fmt(visit.outstandingAmount)}</div>
          </div>
        </div>
      </div>

      <div className="content">
        <div className="section-label">What was the result?</div>
        <div className="outcome-grid">
          {Object.entries(OUTCOME_META).map(([key, { icon, label }]) => (
            <div
              key={key}
              className={`outcome-opt${outcome === key ? ' selected' : ''}`}
              onClick={() => setOutcome(key)}
            >
              <span className="opt-icon">{icon}</span>
              {label}
            </div>
          ))}
        </div>

        {needsAmount && (
          <div className="form-group">
            <label className="form-label">Amount Collected (₹)</label>
            <input className="form-input" type="number" placeholder="Enter amount"
              value={amount} onChange={e => setAmount(e.target.value)} />
          </div>
        )}

        {needsPtp && (
          <div className="form-group">
            <label className="form-label">Promised Payment Date</label>
            <input className="form-input" type="date"
              value={ptpDate} onChange={e => setPtpDate(e.target.value)} />
          </div>
        )}

        <div className="form-group">
          <label className="form-label">Notes (optional)</label>
          <textarea className="form-input" rows={3} placeholder="Add any relevant notes…"
            value={notes} onChange={e => setNotes(e.target.value)} />
        </div>

        <div className="form-group">
          <label className="form-label">Location Verification</label>
          {gpsStatus === 'idle' && (
            <button className="btn btn-outline" onClick={getGPS}>📍 Capture GPS Location</button>
          )}
          {gpsStatus === 'getting' && <Spinner text="Getting location…" />}
          {gpsStatus === 'got' && (
            <div className="gps-pill gps-got">
              📍 Location captured · {coords.lat.toFixed(4)}, {coords.lng.toFixed(4)}
            </div>
          )}
          {gpsStatus === 'error' && (
            <div className="gps-pill gps-err">
              ✕ GPS unavailable — proceeding without location
            </div>
          )}
        </div>
      </div>

      <div className="btn-wrap">
        <button className="btn btn-primary" onClick={submit} disabled={submitting || !outcome}>
          {submitting ? 'Submitting…' : '✓ Submit Outcome'}
        </button>
        <button className="btn btn-ghost" onClick={onBack}>Cancel</button>
      </div>
    </div>
  )
}

// ─── Root ─────────────────────────────────────────────────────────────────────

export default function App() {
  const [screen, setScreen] = useState('login')
  const [agent, setAgent]   = useState(null)
  const [visit, setVisit]   = useState(null)
  const [visits, setVisits] = useState(null)

  useEffect(() => {
    const raw = localStorage.getItem('fiq_session')
    if (raw) {
      try {
        const session = JSON.parse(raw)
        if (session.token && session.agentId) {
          setAgent(session)
          setScreen('route')
        }
      } catch { /* stale */ }
    }
  }, [])

  const logout = () => {
    localStorage.removeItem('fiq_session')
    setAgent(null)
    setVisits(null)
    setVisit(null)
    setScreen('login')
  }

  if (screen === 'login')
    return <LoginScreen onLogin={s => { setAgent(s); setVisits(null); setScreen('route') }} />

  if (screen === 'route')
    return <RouteList agent={agent} preloadedVisits={visits}
      onLogout={logout}
      onVisitClick={v => { setVisit(v); setScreen('detail') }} />

  if (screen === 'detail')
    return <VisitDetail visit={visit} agent={agent}
      onBack={() => setScreen('route')}
      onOutcome={v => { setVisit(v); setScreen('outcome') }}
      onRouteUpdated={u => { setVisits(u); setScreen('route') }} />

  if (screen === 'outcome')
    return <LogOutcome visit={visit}
      onBack={() => setScreen('detail')}
      onDone={u => { setVisit(u); setScreen('detail') }} />
}